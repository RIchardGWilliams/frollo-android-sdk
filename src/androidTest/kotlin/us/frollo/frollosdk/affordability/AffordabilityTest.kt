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

package us.frollo.frollosdk.affordability

import com.google.gson.Gson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.BaseAndroidTest
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.extensions.fromJson
import us.frollo.frollosdk.model.api.affordability.ExportType
import us.frollo.frollosdk.model.coredata.user.User
import us.frollo.frollosdk.network.api.AffordabilityAPI
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AffordabilityTest : BaseAndroidTest() {

    override fun initSetup(daOAuth2Login: Boolean) {
        super.initSetup(daOAuth2Login)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
    }

    @Test
    fun testFinancialPassport() {
        initSetup()
        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.financial_passport_response)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == AffordabilityAPI.URL_FINANCIAL_PASSPORT) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        affordability.getFinancialPassport { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)
            val model = resource.data
            assertNotNull(model)
            assertEquals(5, model?.accounts?.size)
            assertEquals(20902L, model?.accounts?.first()?.accountId)
            assertEquals(BigDecimal(-10798.36).setScale(2, RoundingMode.HALF_EVEN), model?.summary?.totals?.expenses)
            assertEquals(BigDecimal(-1542.62).setScale(2, RoundingMode.HALF_EVEN), model?.expenses?.averages?.fortnightly)
            assertEquals(BigDecimal(-799.00).setScale(2, RoundingMode.HALF_EVEN), model?.expenses?.averagesThreeMonths?.weekly)
            assertEquals(8, model?.expenses?.breakdown?.size)
            assertEquals("Groceries", model?.expenses?.breakdown?.first()?.category?.name)
            assertEquals(1, model?.income?.breakdown?.size)
            assertEquals("SALARY", model?.income?.breakdown?.first()?.category?.name)
            assertEquals(1, model?.income?.breakdown?.first()?.recurringTransactions?.size)
            assertEquals("2022-01-29T14:00:00", model?.income?.breakdown?.first()?.recurringTransactions?.first()?.firstTransactionDate)
            assertEquals(3, model?.income?.breakdown?.first()?.recurringTransactions?.first()?.transactionIds?.size)
            assertEquals(1, model?.assets?.breakdown?.size)
            assertEquals(3, model?.assets?.breakdown?.first()?.accounts?.size)
            assertEquals(20904L, model?.assets?.breakdown?.first()?.accounts?.first()?.id)
            assertEquals(BigDecimal(0.00).setScale(2, RoundingMode.HALF_EVEN), model?.liabilities?.summary?.weekly?.creditCardMinimumRepayment)
            assertEquals(2, model?.liabilities?.breakdown?.size)
            assertEquals("credit", model?.liabilities?.breakdown?.first()?.name)
            assertEquals(1, model?.liabilities?.breakdown?.first()?.accounts?.size)
            assertEquals(BigDecimal(-187.50).setScale(2, RoundingMode.HALF_EVEN), model?.responsibleLendingIndicators?.monthlyExpAtmWithdraw)
            signal.countDown()
        }
        val request = mockServer.takeRequest()
        assertEquals(AffordabilityAPI.URL_FINANCIAL_PASSPORT, request.trimmedPath)
        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFinancialPassportFailsIfLoggedOut() {
        initSetup()
        val signal = CountDownLatch(1)
        clearLoggedInPreferences()
        affordability.getFinancialPassport { resource ->
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
    fun testFinancialPassportExport() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.user_details_complete)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "${AffordabilityAPI.URL_FINANCIAL_PASSPORT_EXPORT}?type=pdf") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        affordability.exportFinancialPassport(ExportType.PDF) { resource ->
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
        assertEquals("${AffordabilityAPI.URL_FINANCIAL_PASSPORT_EXPORT}?type=pdf", request.trimmedPath)
        signal.await(3, TimeUnit.SECONDS)
        tearDown()
    }

    @Test
    fun testFinancialPassportExportFailsIfLoggedOut() {
        initSetup()
        val signal = CountDownLatch(1)
        clearLoggedInPreferences()

        affordability.exportFinancialPassport(ExportType.PDF) { resource ->
            assertNotNull(resource)
            assertEquals(DataErrorType.AUTHENTICATION, (resource.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (resource.error as DataError).subType)
            signal.countDown()
        }
        signal.await(3, TimeUnit.SECONDS)
        tearDown()
    }

    @Test
    fun testFetchAssetsLiabilitiesConfig() {
        initSetup()
        val signal = CountDownLatch(1)

        affordability.fetchAssetsLiabilitiesConfig { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)
            val model = resource.data
            assertNotNull(model)
            assertEquals("PROPERTY", model?.assets?.first()?.type?.name)
            assertEquals("https://picsum.photos/100", model?.assets?.first()?.displayImageUrl)
            assertEquals(7, model?.assets?.first()?.zoning?.size)
            assertEquals("residential", model?.assets?.first()?.zoning?.first()?.zone?.name)
            assertEquals(18, model?.assets?.first()?.zoning?.first()?.propertyTypes?.size)
            assertEquals("COMPANY_TITLE_UNIT", model?.assets?.first()?.zoning?.first()?.propertyTypes?.first()?.name)
            assertEquals("https://picsum.photos/100", model?.assets?.first()?.zoning?.first()?.displayImageUrl)
            assertEquals("CASH_MANAGEMENT", model?.assets?.first()?.accountSubTypes?.first()?.name)
            assertEquals(14, model?.liabilities?.accountSubTypes?.size)
            assertEquals("COMMERCIAL_BILL", model?.liabilities?.accountSubTypes?.first()?.name)

            signal.countDown()
        }

        tearDown()
    }
}
