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

package us.frollo.frollosdk.consents

import com.jraska.livedata.test
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.BaseAndroidTest
import us.frollo.frollosdk.base.PaginatedResult
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.mapping.toCDRConfiguration
import us.frollo.frollosdk.mapping.toConsent
import us.frollo.frollosdk.mapping.toProvider
import us.frollo.frollosdk.mapping.toProviderAccount
import us.frollo.frollosdk.model.coredata.aggregation.providers.CDRProductCategory
import us.frollo.frollosdk.model.coredata.cdr.CDRModel
import us.frollo.frollosdk.model.coredata.cdr.CDRPartyType
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus
import us.frollo.frollosdk.model.testCDRConfigurationData
import us.frollo.frollosdk.model.testConsentCreateFormData
import us.frollo.frollosdk.model.testConsentResponseData
import us.frollo.frollosdk.model.testConsentUpdateFormData
import us.frollo.frollosdk.model.testProviderAccountResponseData
import us.frollo.frollosdk.model.testProviderResponseData
import us.frollo.frollosdk.network.api.CdrAPI
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ConsentTest : BaseAndroidTest() {

    companion object {
        private const val CDR_CONFIG_EXTERNAL_ID = "frollo-default"
    }

    override fun initSetup(daOAuth2Login: Boolean) {
        super.initSetup(daOAuth2Login)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
    }

    // Consent Tests

    @Test
    fun testFetchConsentByID() {
        initSetup()

        val data = testConsentResponseData()
        val list = mutableListOf(testConsentResponseData(), data, testConsentResponseData())
        database.consents().insertAll(*list.map { it.toConsent() }.toList().toTypedArray())

        val testObserver = consents.fetchConsent(data.consentId).test()
        testObserver.awaitValue()
        assertEquals(data.consentId, testObserver.value()?.consentId)

        tearDown()
    }

    @Test
    fun testFetchConsents() {
        initSetup()

        val data1 = testConsentResponseData(providerId = 123, providerAccountId = 234, status = ConsentStatus.ACTIVE)
        val data2 = testConsentResponseData(providerId = 123, providerAccountId = 235, status = ConsentStatus.PENDING)
        val data3 = testConsentResponseData(providerId = 123, providerAccountId = 236, status = ConsentStatus.WITHDRAWN)
        val data4 = testConsentResponseData(providerId = 124, providerAccountId = 237, status = ConsentStatus.ACTIVE)
        val list = mutableListOf(data1, data2, data3, data4)

        database.consents().insertAll(*list.map { it.toConsent() }.toList().toTypedArray())

        var testObserver = consents.fetchConsents().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(4, testObserver.value()?.size)

        testObserver = consents.fetchConsents(providerId = 123).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(3, testObserver.value()?.size)

        testObserver = consents.fetchConsents(status = ConsentStatus.ACTIVE).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(2, testObserver.value()?.size)

        testObserver = consents.fetchConsents(providerId = 123, providerAccountId = 235, status = ConsentStatus.PENDING).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(1, testObserver.value()?.size)

        tearDown()
    }

    @Test
    fun testFetchConsentByIDWithRelation() {
        initSetup()

        database.providers().insert(testProviderResponseData(providerId = 123).toProvider())
        database.providerAccounts().insert(testProviderAccountResponseData(providerAccountId = 234, providerId = 123).toProviderAccount())
        database.providerAccounts().insert(testProviderAccountResponseData(providerAccountId = 235, providerId = 123).toProviderAccount())
        database.consents().insert(testConsentResponseData(consentId = 345, providerId = 123, providerAccountId = 234).toConsent())
        database.consents().insert(testConsentResponseData(consentId = 346, providerId = 123, providerAccountId = 235).toConsent())

        val testObserver = consents.fetchConsentWithRelation(consentId = 345).test()
        testObserver.awaitValue()

        val model = testObserver.value()

        assertEquals(123L, model?.provider?.provider?.providerId)
        assertEquals(1, model?.providerAccounts?.size)
        assertEquals(234L, model?.providerAccount?.providerAccount?.providerAccountId)
        assertEquals(345L, model?.consent?.consentId)

        tearDown()
    }

    @Test
    fun testFetchConsentsWithRelation() {
        initSetup()

        database.providers().insert(testProviderResponseData(providerId = 123).toProvider())
        database.providerAccounts().insert(testProviderAccountResponseData(providerAccountId = 234, providerId = 123).toProviderAccount())
        database.providerAccounts().insert(testProviderAccountResponseData(providerAccountId = 235, providerId = 123).toProviderAccount())
        database.consents().insert(testConsentResponseData(consentId = 345, providerId = 123, providerAccountId = 234).toConsent())
        database.consents().insert(testConsentResponseData(consentId = 346, providerId = 123, providerAccountId = 235).toConsent())

        val testObserver = consents.fetchConsentsWithRelation().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(2, testObserver.value()?.size)

        val model = testObserver.value()?.first()

        assertEquals(123L, model?.provider?.provider?.providerId)
        assertEquals(1, model?.providerAccounts?.size)
        assertEquals(234L, model?.providerAccount?.providerAccount?.providerAccountId)
        assertEquals(345L, model?.consent?.consentId)

        tearDown()
    }

    @Test
    fun testRefreshConsentsIsCached() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.consents_page_1)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == CdrAPI.URL_CDR_CONSENTS) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        consents.refreshConsentsWithPagination { result ->
            assertTrue(result is PaginatedResult.Success)

            val testObserver = consents.fetchConsents().test()
            testObserver.awaitValue()
            val models = testObserver.value()
            assertNotNull(models)
            assertEquals(8, models?.size)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(CdrAPI.URL_CDR_CONSENTS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshConsentsFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        consents.refreshConsentsWithPagination { result ->
            assertTrue(result is PaginatedResult.Error)
            assertNotNull((result as PaginatedResult.Error).error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshPaginatedConsents() {
        initSetup()

        val requestPath1 = "${CdrAPI.URL_CDR_CONSENTS}?size=8"
        val requestPath2 = "${CdrAPI.URL_CDR_CONSENTS}?after=8&size=8"

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == requestPath1) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.consents_page_1))
                    } else if (request.trimmedPath == requestPath2) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.consents_page_2))
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        // Insert some stale contacts
        val data1 = testConsentResponseData(consentId = 14)
        val data2 = testConsentResponseData(consentId = 15)
        val list = mutableListOf(data1, data2)
        database.consents().insertAll(*list.map { it.toConsent() }.toList().toTypedArray())

        consents.refreshConsentsWithPagination(size = 8) { result1 ->
            assertTrue(result1 is PaginatedResult.Success)
            assertNull((result1 as PaginatedResult.Success).paginationInfo?.before)
            assertEquals(8L, result1.paginationInfo?.after)

            consents.refreshConsentsWithPagination(size = 8, after = result1.paginationInfo?.after) { result2 ->
                assertTrue(result2 is PaginatedResult.Success)
                assertEquals(8L, (result2 as PaginatedResult.Success).paginationInfo?.before)
                assertNull(result2.paginationInfo?.after)

                val testObserver = consents.fetchConsents().test()
                testObserver.awaitValue()
                val models = testObserver.value()
                assertNotNull(models)
                assertEquals(11, models?.size)

                // Verify that the stale consents are deleted from the database
                assertEquals(0, models?.filter { it.consentId == 14L && it.consentId == 15L }?.size)

                signal.countDown()
            }
        }

        signal.await(3, TimeUnit.SECONDS)

        assertEquals(2, mockServer.requestCount)

        tearDown()
    }

    @Test
    fun testRefreshAllConsents() {
        initSetup()

        val requestPath1 = CdrAPI.URL_CDR_CONSENTS
        val requestPath2 = "${CdrAPI.URL_CDR_CONSENTS}?after=8"

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == requestPath1) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.consents_page_1))
                    } else if (request.trimmedPath == requestPath2) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(readStringFromJson(app, R.raw.consents_page_2))
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        // Insert some stale contacts
        val data1 = testConsentResponseData(consentId = 14)
        val data2 = testConsentResponseData(consentId = 15)
        val list = mutableListOf(data1, data2)
        database.consents().insertAll(*list.map { it.toConsent() }.toList().toTypedArray())

        consents.refreshAllConsents { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = consents.fetchConsents().test()
            testObserver.awaitValue()
            val models = testObserver.value()
            assertNotNull(models)
            assertEquals(11, models?.size)

            // Verify that the stale consents are deleted from the database
            assertEquals(0, models?.filter { it.consentId == 14L && it.consentId == 15L }?.size)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        assertEquals(2, mockServer.requestCount)

        tearDown()
    }

    @Test
    fun testRefreshConsentByID() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.consent_id_353)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "cdr/consents/353") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        consents.refreshConsent(353L) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = consents.fetchConsent(353L).test()
            testObserver.awaitValue()
            val model = testObserver.value()
            assertNotNull(model)
            assertEquals(353L, model?.consentId)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("cdr/consents/353", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshConsentByIdFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        consents.refreshConsent(353L) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testSubmitConsent() {
        initSetup()

        val requestPath1 = CdrAPI.URL_CDR_CONSENTS
        val requestPath2 = "${CdrAPI.URL_CDR_CONSENTS}?after=8"

        val signal = CountDownLatch(1)

        val submitBody = readStringFromJson(app, R.raw.consent_created)
        val refreshPage1 = readStringFromJson(app, R.raw.consents_page_1)
        val refreshPage2 = readStringFromJson(app, R.raw.consents_page_2)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == requestPath1) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(if (request.method == "POST") submitBody else refreshPage1)
                    } else if (request.trimmedPath == requestPath2) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(refreshPage2)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        consents.submitConsent(testConsentCreateFormData()) { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)
            assertEquals(410L, resource.data)

            val testObserver = consents.fetchConsents().test()
            testObserver.awaitValue()
            val models = testObserver.value()
            assertNotNull(models)
            assertEquals(11, models?.size)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testSubmitConsentFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        consents.submitConsent(testConsentCreateFormData()) { resource ->
            assertEquals(Resource.Status.ERROR, resource.status)
            assertNotNull(resource.error)
            assertEquals(DataErrorType.AUTHENTICATION, (resource.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (resource.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateConsent() {
        initSetup()

        val signal1 = CountDownLatch(1)

        val refreshBody = readStringFromJson(app, R.raw.consent_fetched)
        val updateBody = readStringFromJson(app, R.raw.consent_updated)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "cdr/consents/39") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(if (request.method == "PUT") updateBody else refreshBody)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        consents.refreshConsent(consentId = 39) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = consents.fetchConsent(consentId = 39).test()
            testObserver.awaitValue()
            assertEquals(15642000L, testObserver.value()?.sharingDuration)

            signal1.countDown()
        }

        signal1.await(3, TimeUnit.SECONDS)

        val signal2 = CountDownLatch(1)

        consents.updateConsent(consentId = 39, testConsentUpdateFormData()) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = consents.fetchConsent(consentId = 39).test()
            testObserver.awaitValue()
            assertEquals(17532000L, testObserver.value()?.sharingDuration)

            signal2.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("cdr/consents/39", request.trimmedPath)

        signal2.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateConsentFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        consents.updateConsent(consentId = 39, testConsentUpdateFormData()) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchProductsByAccountID() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.products_account_id_542)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "${CdrAPI.URL_CDR_PRODUCTS}?account_id=542") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        consents.fetchCDRProducts(accountId = 542) { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            val models = resource.data
            assertNotNull(models)
            assertEquals(131, models?.size)
            assertEquals(22580L, models?.first()?.providerId)
            assertEquals(CDRProductCategory.RESIDENTIAL_MORTGAGES, models?.first()?.productCategory)
            assertEquals("Fixed Rate Investment Property Loan Interest Only", models?.first()?.name)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("${CdrAPI.URL_CDR_PRODUCTS}?account_id=542", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchProductsByAccountIdFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        consents.fetchCDRProducts(accountId = 542) { resource ->
            assertEquals(Resource.Status.ERROR, resource.status)
            assertNotNull(resource.error)
            assertEquals(DataErrorType.AUTHENTICATION, (resource.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (resource.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchProductByID() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.product_id_65)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "cdr/products/65") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        consents.fetchCDRProduct(productId = 65) { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            val model = resource.data
            assertNotNull(model)
            assertEquals(65L, model?.productId)
            assertEquals(CDRProductCategory.TRANSACTION_AND_SAVINGS_ACCOUNTS, model?.productCategory)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("cdr/products/65", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchProductByIdFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        consents.fetchCDRProducts(accountId = 542) { resource ->
            assertEquals(Resource.Status.ERROR, resource.status)
            assertNotNull(resource.error)
            assertEquals(DataErrorType.AUTHENTICATION, (resource.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (resource.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchCDRConfiguration() {
        initSetup()

        database.cdrConfiguration().insert(
            testCDRConfigurationData(
                configId = 100,
                externalId = CDR_CONFIG_EXTERNAL_ID
            ).toCDRConfiguration()
        )

        val testObserver = consents.fetchCDRConfiguration(CDR_CONFIG_EXTERNAL_ID).test()
        testObserver.awaitValue()
        assertEquals(100L, testObserver.value()?.configId)

        tearDown()
    }

    @Test
    fun testRefreshCDRConfiguration() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.cdr_configuration)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "config/cdr/$CDR_CONFIG_EXTERNAL_ID") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        consents.refreshCDRConfiguration(CDR_CONFIG_EXTERNAL_ID) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = consents.fetchCDRConfiguration(CDR_CONFIG_EXTERNAL_ID).test()
            testObserver.awaitValue()
            val model = testObserver.value()
            assertNotNull(model)
            assertEquals(1L, model?.configId)
            assertEquals("support@frollo.us", model?.supportEmail)
            assertEquals(3, model?.sharingDurations?.size)
            assertEquals(2, model?.permissions?.size)
            assertEquals("frollo-default", model?.externalId)
            assertEquals("Frollo", model?.displayName)
            assertEquals("https://example.com", model?.cdrPolicyUrl)
            assertEquals(CDRModel.AFFILIATE, model?.model)
            assertEquals(12345L, model?.relatedParties?.first()?.partyId)
            assertEquals("ACME Inc", model?.relatedParties?.first()?.name)
            assertEquals("Enhance stuff", model?.relatedParties?.first()?.description)
            assertEquals(CDRPartyType.OSP, model?.relatedParties?.first()?.type)
            assertEquals("AFF0001", model?.relatedParties?.first()?.adrId)
            assertEquals("CDR Policy", model?.relatedParties?.first()?.policy?.name)
            assertEquals("https://example.com", model?.relatedParties?.first()?.policy?.url)
            assertEquals(7890000L, model?.sharingUseDuration)
            assertEquals(53, model?.initialSyncWindowWeeks)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("config/cdr/$CDR_CONFIG_EXTERNAL_ID", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshCDRConfigurationFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        consents.refreshCDRConfiguration(CDR_CONFIG_EXTERNAL_ID) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }
}
