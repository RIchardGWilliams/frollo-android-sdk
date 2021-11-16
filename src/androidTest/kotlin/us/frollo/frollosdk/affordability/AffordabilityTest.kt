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
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
class AffordabilityTest : BaseAndroidTest() {

    override fun initSetup() {
        super.initSetup()

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
        mockServer.dispatcher = (object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == "affordability/financialpassport") {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            })

        affordability.getFinancialPassport { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)
            val model = resource.data
            assertNotNull(model)
            assertEquals(11029L, model?.accounts?.get(0)?.accountId)
            assertEquals(BigDecimal(-316.44).setScale(2, RoundingMode.HALF_EVEN), model?.summary?.totals?.expenses)
            assertEquals("Recreation", model?.incomeExpenses?.breakdown?.get(0)?.category?.name)
            assertEquals("Superannuation", model?.income?.breakdown?.get(0)?.category?.name)
            assertEquals(11033L, model?.assetsLiabilities?.breakdown?.get(0)?.accounts?.get(0)?.id)
            assertEquals("Loans", model?.liabilities?.breakdown?.get(0)?.name)
            assertEquals(BigDecimal(1025.40).setScale(2, RoundingMode.HALF_EVEN), model?.riskScores?.get(0)?.creditsNoTransfers30Days)
            signal.countDown()
        }
        val request = mockServer.takeRequest()
        assertEquals("affordability/financialpassport", request.trimmedPath)
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
}