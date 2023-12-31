/*
 * Copyright 2019 Frollo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.frollo.frollosdk.user

import com.google.gson.Gson
import com.jraska.livedata.test
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.BaseAndroidTest
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.error.APIError
import us.frollo.frollosdk.error.APIErrorType
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.extensions.fromJson
import us.frollo.frollosdk.mapping.toAddress
import us.frollo.frollosdk.mapping.toExternalParty
import us.frollo.frollosdk.mapping.toUser
import us.frollo.frollosdk.model.api.shared.APIErrorCode
import us.frollo.frollosdk.model.api.user.UserResponse
import us.frollo.frollosdk.model.coredata.contacts.PayIDType
import us.frollo.frollosdk.model.coredata.user.Attribution
import us.frollo.frollosdk.model.coredata.user.Gender
import us.frollo.frollosdk.model.coredata.user.HouseholdType
import us.frollo.frollosdk.model.coredata.user.Industry
import us.frollo.frollosdk.model.coredata.user.Occupation
import us.frollo.frollosdk.model.coredata.user.OtpMethodType
import us.frollo.frollosdk.model.coredata.user.TFNStatus
import us.frollo.frollosdk.model.coredata.user.UserStatus
import us.frollo.frollosdk.model.coredata.user.payid.UserPayIdAccountStatus
import us.frollo.frollosdk.model.coredata.user.payid.UserPayIdOTPMethodType
import us.frollo.frollosdk.model.coredata.user.payid.UserPayIdStatus
import us.frollo.frollosdk.model.testAddressResponseData
import us.frollo.frollosdk.model.testExternalPartyResponseData
import us.frollo.frollosdk.model.testUserRequestData
import us.frollo.frollosdk.model.testUserResponseData
import us.frollo.frollosdk.network.api.DeviceAPI
import us.frollo.frollosdk.network.api.UserAPI
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.randomString
import us.frollo.frollosdk.testutils.randomUUID
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.util.Date
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UserManagementTest : BaseAndroidTest() {

    @Test
    fun testFetchUser() {
        initSetup()

        database.users().insert(testUserResponseData(userId = 12345).toUser())

        val testObserver2 = userManagement.fetchUser().test()
        testObserver2.awaitValue()
        assertNotNull(testObserver2.value().data)
        assertEquals(12345L, testObserver2.value().data?.userId)

        tearDown()
    }

    @Test
    fun testRegisterUser() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.user_details_complete)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_REGISTER) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.registerUser(
            firstName = "Frollo",
            lastName = "User",
            mobileNumber = "0412345678",
            dateOfBirth = Date(),
            email = "user@frollo.us",
            password = "password"
        ) { result ->

            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = userManagement.fetchUser().test()
            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)

            val expectedResponse = Gson().fromJson<UserResponse>(body)
            assertEquals(expectedResponse?.toUser(), testObserver.value().data)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_REGISTER, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRegisterUserInvalid() {
        initSetup()

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_REGISTER) {
                        return MockResponse()
                            .setResponseCode(409)
                            .setBody(readStringFromJson(app, R.raw.error_duplicate))
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.registerUser(
            firstName = "Frollo",
            lastName = "User",
            mobileNumber = "0412345678",
            dateOfBirth = Date(),
            email = "user@frollo.us",
            password = "password"
        ) { result ->

            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)

            val testObserver = userManagement.fetchUser().test()
            testObserver.awaitValue()
            assertNull(testObserver.value().data)

            assertEquals(APIErrorType.ALREADY_EXISTS, (result.error as APIError).type)
            assertFalse(oAuth2Authentication.loggedIn)

            assertNull(preferences.encryptedAccessToken)
            assertNull(preferences.encryptedRefreshToken)
            assertEquals(-1L, preferences.accessTokenExpiry)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshUser() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.user_details_complete)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_USER_DETAILS) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.refreshUser { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = userManagement.fetchUser().test()
            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)

            val user = testObserver.value().data
            assertEquals(12345L, user?.userId)
            assertEquals("Jacob", user?.firstName)
            assertEquals("Frollo", user?.lastName)
            assertEquals("M", user?.middleNames)
            assertEquals(true, user?.emailVerified)
            assertEquals(UserStatus.ACTIVE, user?.status)
            assertEquals("AUD", user?.primaryCurrency)
            assertEquals(Gender.MALE, user?.gender)
            assertEquals("1990-01-10", user?.dateOfBirth)
            assertEquals(TFNStatus.RECEIVED, user?.tfnStatus)
            assertEquals(0L, user?.residentialAddress?.addressId)
            assertEquals("Frollo, Level 33, 100 Mount St, North Sydney, NSW, 2060, Australia", user?.residentialAddress?.longForm)
            assertEquals(1L, user?.mailingAddress?.addressId)
            assertEquals("U 1 33 Harrow Road, Bexley, NSW, 2216", user?.mailingAddress?.longForm)
            assertEquals(2L, user?.previousAddress?.addressId)
            assertEquals("U 5 21 Hampton Court Road, Carlton, NSW, 2218", user?.previousAddress?.longForm)
            assertEquals(HouseholdType.SINGLE, user?.householdType)
            assertEquals(Occupation.COMMUNITY_AND_PERSONAL_SERVICE_WORKERS, user?.occupation)
            assertEquals(Industry.ELECTRICITY_GAS_WATER_AND_WASTE_SERVICES, user?.industry)
            assertEquals(2, user?.householdSize)
            assertEquals("aggregation", user?.features?.get(0)?.feature)
            assertEquals(true, user?.features?.get(0)?.enabled)
            assertEquals("1234567890", user?.facebookId)
            assertEquals("2019-01-01", user?.registrationDate)
            assertEquals("0411111111", user?.mobileNumber)
            assertEquals(true, user?.validPassword)
            assertEquals("Organic", user?.attribution?.network)
            assertEquals(3, user?.registerSteps?.size)
            assertEquals("survey", user?.registerSteps?.get(1)?.key)
            assertEquals(1, user?.registerSteps?.get(1)?.index)
            assertEquals(true, user?.registerSteps?.get(1)?.required)
            assertEquals(false, user?.registerSteps?.get(1)?.completed)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_USER_DETAILS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateUser() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.user_details_complete)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_USER_DETAILS) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.updateUser(testUserRequestData()) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = userManagement.fetchUser().test()
            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)

            val expectedResponse = Gson().fromJson<UserResponse>(body)!!
            assertEquals(expectedResponse.toUser(), testObserver.value().data)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_USER_DETAILS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateUserFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_USER_DETAILS) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.user_details_complete))
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.updateUser(testUserRequestData()) { result ->
            assertFalse(oAuth2Authentication.loggedIn)

            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)

            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        assertEquals(0, mockServer.requestCount)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateAttribution() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.user_details_complete)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_USER_DETAILS) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.updateAttribution(Attribution(campaign = randomString(8))) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = userManagement.fetchUser().test()
            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)

            val expectedResponse = Gson().fromJson<UserResponse>(body)!!
            assertEquals(expectedResponse.toUser(), testObserver.value().data)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_USER_DETAILS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testChangePassword() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_CHANGE_PASSWORD) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.changePassword(currentPassword = randomUUID(), newPassword = randomUUID()) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_CHANGE_PASSWORD, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testChangePasswordFailsIfTooShort() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_CHANGE_PASSWORD) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.changePassword(currentPassword = randomUUID(), newPassword = "1234") { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)

            assertEquals(DataErrorType.API, (result.error as DataError).type)
            assertEquals(DataErrorSubType.PASSWORD_TOO_SHORT, (result.error as DataError).subType)

            signal.countDown()
        }

        assertEquals(0, mockServer.requestCount)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testDeleteUser() {
        initSetup()

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_DELETE_USER) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        userManagement.deleteUser { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            assertFalse(oAuth2Authentication.loggedIn)
            assertNull(preferences.encryptedAccessToken)
            assertNull(preferences.encryptedRefreshToken)
            assertEquals(-1, preferences.accessTokenExpiry)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_DELETE_USER, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testDeleteUserFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_DELETE_USER) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.deleteUser { result ->
            assertFalse(oAuth2Authentication.loggedIn)

            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)

            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        assertEquals(0, mockServer.requestCount)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testResetPassword() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_PASSWORD_RESET) {
                        return MockResponse()
                            .setResponseCode(202)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.resetPassword(email = "user@frollo.us") { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_PASSWORD_RESET, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateDevice() {
        initSetup()

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == DeviceAPI.URL_DEVICE) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        userManagement.updateDevice(notificationToken = "SomeToken12345") { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(DeviceAPI.URL_DEVICE, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateDeviceCompliance() {
        initSetup()

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == DeviceAPI.URL_DEVICE) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        userManagement.updateDeviceCompliance(true) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(DeviceAPI.URL_DEVICE, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testAuthenticatingRequestManually() {
        initSetup()

        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val request = userManagement.authenticateRequest(
            Request.Builder()
                .url("http://api.example.com/")
                .build()
        )
        assertNotNull(request)
        assertEquals("http://api.example.com/", request.url.toString())
        assertEquals("Bearer ExistingAccessToken", request.header("Authorization"))

        tearDown()
    }

    @Test
    fun testMigrateUser() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_MIGRATE_USER) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.migrateUser(password = randomUUID()) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            assertFalse(preferences.loggedIn)
            assertNull(preferences.encryptedAccessToken)
            assertNull(preferences.encryptedRefreshToken)
            assertEquals(-1, preferences.accessTokenExpiry)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_MIGRATE_USER, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testMigrateUserFailsMigrationError() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        val expiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
        preferences.accessTokenExpiry = expiry

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_MIGRATE_USER) {
                        return MockResponse()
                            .setResponseCode(400)
                            .setBody(readStringFromJson(app, R.raw.error_migration))
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.migrateUser(password = randomUUID()) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)

            assertEquals(APIErrorType.MIGRATION_FAILED, (result.error as APIError).type)
            assertEquals(APIErrorCode.MIGRATION_FAILED, (result.error as APIError).errorCode)

            assertTrue(preferences.loggedIn)
            assertEquals("ExistingAccessToken", keystore.decrypt(preferences.encryptedAccessToken))
            assertEquals("ExistingRefreshToken", keystore.decrypt(preferences.encryptedRefreshToken))
            assertEquals(expiry, preferences.accessTokenExpiry)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_MIGRATE_USER, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testMigrateUserFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        userManagement.migrateUser(password = randomUUID()) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_REFRESH_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        assertEquals(0, mockServer.requestCount)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testMigrateUserFailsMissingRefreshToken() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.resetEncryptedRefreshToken()
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        val expiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
        preferences.accessTokenExpiry = expiry

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_MIGRATE_USER) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.migrateUser(password = randomUUID()) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)

            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_REFRESH_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testMigrateUserFailsIfTooShort() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        val expiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
        preferences.accessTokenExpiry = expiry

        userManagement.migrateUser(password = "1234") { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)

            assertEquals(DataErrorType.API, (result.error as DataError).type)
            assertEquals(DataErrorSubType.PASSWORD_TOO_SHORT, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRequestNewOtp() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_REQUEST_OTP) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.requestNewOtp(OtpMethodType.SMS) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_REQUEST_OTP, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchUnconfirmedUserDetails() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.user_confirm_details)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_CONFIRM_DETAILS) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.fetchUnconfirmedUserDetails { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            assertEquals("+64111111111", resource.data?.mobileNumber)
            assertEquals("user@example.com", resource.data?.email)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_CONFIRM_DETAILS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testConfirmUserDetails() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_CONFIRM_DETAILS) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.confirmUserDetails(mobileNumber = "+64111111111", securityCode = "123456") { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_CONFIRM_DETAILS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchPayIDs() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.user_get_payids)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_PAYID) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.fetchPayIds { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            val models = resource.data
            assertEquals(2, models?.size)
            assertEquals("+6412345678", models?.get(0)?.payId)
            assertEquals(UserPayIdStatus.AVAILABLE, models?.get(0)?.status)
            assertEquals(PayIDType.MOBILE, models?.get(0)?.type)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_PAYID, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchPayIDsForAccount() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.user_get_payids_for_account)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "user/payid/account/12345") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.fetchPayIdsForAccount(12345L) { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            val models = resource.data
            assertEquals(2, models?.size)
            assertEquals("+61411111111", models?.get(0)?.payId)
            assertEquals(UserPayIdAccountStatus.ACTIVE, models?.get(0)?.status)
            assertEquals(PayIDType.MOBILE, models?.get(0)?.type)
            assertEquals("Frollo", models?.get(0)?.name)
            assertEquals("2021-01-28T05:24:40.597Z", models?.get(0)?.createdAt)
            assertEquals("2021-01-28T05:24:40.597Z", models?.get(0)?.updatedAt)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("user/payid/account/12345", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRequestPayIdOtp() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.user_request_payid_otp)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_PAYID_OTP) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.requestOtpForPayIdRegistration("user@example.com", UserPayIdOTPMethodType.EMAIL) { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            val models = resource.data
            assertEquals("VE86849f8805b0906b4a8360bce8c025db", resource.data?.trackingId)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_PAYID_OTP, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRegisterPayId() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_PAYID) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.registerPayId(
            accountId = 325,
            payId = "+61411111111",
            type = PayIDType.MOBILE,
            trackingId = "VE20db0310501c4d7cc347c8d897967039",
            otpCode = "444684"
        ) { resource ->
            assertEquals(Result.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_PAYID, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRemovePayId() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_PAYID_REMOVE) {
                        return MockResponse()
                            .setResponseCode(204)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.removePayId(
            payId = "+61411111111",
            type = PayIDType.MOBILE
        ) { resource ->
            assertEquals(Result.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_PAYID_REMOVE, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testSendFeedback() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == DeviceAPI.URL_LOG) {
                        return MockResponse()
                            .setResponseCode(201)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.sendFeedback(
            message = "App with good user experience"
        ) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(DeviceAPI.URL_LOG, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testSendFeedbackFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        userManagement.sendFeedback("App with good user experience") { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        assertEquals(0, mockServer.requestCount)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUserLinkToAddress() {
        initSetup()

        database.addresses().insert(testAddressResponseData(addressId = 345).toAddress())
        database.addresses().insert(testAddressResponseData(addressId = 346).toAddress())
        database.users().insert(
            testUserResponseData(
                userId = 123,
                residentialAddressId = 345,
                mailingAddressId = 345,
                previousAddressId = 346
            ).toUser()
        )

        val testObserver = userManagement.fetchUserWithRelation().test()

        testObserver.awaitValue()
        val model = testObserver.value()
        assertNotNull(model)
        assertEquals(123L, model?.user?.userId)
        assertEquals(345L, model?.residentialAddress?.addressId)
        assertEquals(345L, model?.mailingAddress?.addressId)
        assertEquals(346L, model?.previousAddress?.addressId)

        tearDown()
    }

    @Test
    fun testUserLinkToExternalParty() {
        initSetup()

        database.externalParty().insert(testExternalPartyResponseData(partyId = 1001).toExternalParty())
        database.users().insert(
            testUserResponseData(
                userId = 123,
                externalPartyId = 1001
            ).toUser()
        )

        val testObserver = userManagement.fetchUserWithRelation().test()

        testObserver.awaitValue()
        val model = testObserver.value()
        assertNotNull(model)
        assertEquals(123L, model?.user?.userId)
        assertEquals(1001L, model?.externalParty?.partyId)

        tearDown()
    }

    @Test
    fun testRequestPasswordlessToken() {
        initSetup()

        val signal = CountDownLatch(1)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        val body = readStringFromJson(app, R.raw.web_authorization_code_valid)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == UserAPI.URL_AUTH_WEB) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        userManagement.requestPasswordlessToken { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            assertEquals("10620cbf-31bb-49d5-9789-d6576a3564fd", resource.data?.authorisationCode)
            assertEquals("2022-03-01T15:43:16.147+11:00", resource.data?.createdAt)
            assertEquals("2022-03-01T16:43:16.117+11:00", resource.data?.expiresAt)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(UserAPI.URL_AUTH_WEB, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRequestPasswordlessTokenFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        userManagement.requestPasswordlessToken { resource ->
            assertEquals(Resource.Status.ERROR, resource.status)
            assertNotNull(resource.error)
            assertEquals(DataErrorType.AUTHENTICATION, (resource.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (resource.error as DataError).subType)

            signal.countDown()
        }

        assertEquals(0, mockServer.requestCount)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }
}
