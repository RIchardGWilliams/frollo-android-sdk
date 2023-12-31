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

package us.frollo.frollosdk.statements

import com.google.gson.Gson
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
import us.frollo.frollosdk.base.PaginatedResultWithData
import us.frollo.frollosdk.base.PaginationInfo
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.extensions.fromJson
import us.frollo.frollosdk.model.api.statements.Statement
import us.frollo.frollosdk.model.api.statements.StatementSortBy
import us.frollo.frollosdk.model.api.statements.StatementType
import us.frollo.frollosdk.model.coredata.shared.OrderType
import us.frollo.frollosdk.model.coredata.user.User
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class StatementsTest : BaseAndroidTest() {

    override fun initSetup(daOAuth2Login: Boolean) {
        super.initSetup(daOAuth2Login)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
    }

    @Test
    fun testFetchStatements() {
        initSetup()

        val signal = CountDownLatch(1)

        val requestPath = "statements?account_ids=1&type=periodic&from_date=2021-01-01&to_date=2021-01-01&before=1&after=2&size=4&sort=account_number&order=asc"

        val body = readStringFromJson(app, R.raw.statements_valid)
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                if (request.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        }

        statements.fetchStatements(listOf(1), StatementType.PERIODIC, "2021-01-01", "2021-01-01", 1, 2, 4, StatementSortBy.ACCOUNT_NUMBER, OrderType.ASC) { resource ->

            assertTrue(resource is PaginatedResultWithData.Success)
            val response = resource as PaginatedResultWithData.Success<PaginationInfo, List<Statement>>

            assertEquals(null, resource.paginationInfo?.before)
            assertEquals(100L, resource.paginationInfo?.after)
            val data = response.data
            assertNotNull(data)
            val statement = data?.get(0)
            assertNotNull(statement)
            assertEquals(statement?.id, 1L)
            assertEquals(statement?.referenceId, "D20210805621541522003")
            assertEquals(statement?.accountId, 2047L)
            assertEquals(statement?.startDate, "2021-08-04")
            assertEquals(statement?.endDate, "2021-08-04")
            assertEquals(statement?.issuedDate, "2021-08-06")

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchStatementsFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        statements.fetchStatements(listOf(1), fromDate = "2020-01-01") { resource ->
            val response = resource as PaginatedResultWithData.Error
            assertNotNull(response)
            assertEquals(DataErrorType.AUTHENTICATION, (resource.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (resource.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchStatement() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.user_details_complete)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "statements/1") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        statements.fetchStatement("1") { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            assertNotNull(resource.data)
            val bodyString = resource.data?.string()?.replace("\n", "")
            bodyString?.let {
                val user: User? = Gson().fromJson(it)
                assertEquals("jacob@frollo.us", user?.email)
            }

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals("statements/1", request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchStatementFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        statements.fetchStatement("asd") { resource ->
            assertNotNull(resource)
            assertEquals(DataErrorType.AUTHENTICATION, (resource.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (resource.error as DataError).subType)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }
}
