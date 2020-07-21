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

package us.frollo.frollosdk.budgets

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
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.extensions.toBudgetCategory
import us.frollo.frollosdk.mapping.toBudget
import us.frollo.frollosdk.mapping.toBudgetPeriod
import us.frollo.frollosdk.model.coredata.budgets.BudgetFrequency
import us.frollo.frollosdk.model.coredata.budgets.BudgetStatus
import us.frollo.frollosdk.model.coredata.budgets.BudgetTrackingStatus
import us.frollo.frollosdk.model.coredata.budgets.BudgetType
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdk.model.testBudgetPeriodResponseData
import us.frollo.frollosdk.model.testBudgetResponseData
import us.frollo.frollosdk.network.api.BudgetsAPI
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BudgetsTest : BaseAndroidTest() {

    override fun initSetup() {
        super.initSetup()

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
    }

    @Test
    fun testFetchMerchantBudgets() {
        initSetup()

        val data1 = testBudgetResponseData(budgetId = 100, type = BudgetType.MERCHANT, typeValue = "65")
        val data2 = testBudgetResponseData(budgetId = 101, type = BudgetType.MERCHANT, typeValue = "65")
        val data3 = testBudgetResponseData(budgetId = 102, type = BudgetType.MERCHANT, typeValue = "63")
        val data4 = testBudgetResponseData(budgetId = 103, type = BudgetType.MERCHANT, typeValue = "62")
        val data5 = testBudgetResponseData(budgetId = 104, type = BudgetType.MERCHANT, typeValue = "61")
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchMerchantBudgets(65L).test()
        val testObserver2 = budgets.fetchMerchantBudgets().test()

        testObserver.awaitValue()
        testObserver2.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(2, testObserver.value().data?.size)
        assertEquals(5, testObserver2.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchMerchantBudgetsFiltered() {
        initSetup()

        val data1 = testBudgetResponseData(budgetId = 100, type = BudgetType.MERCHANT, typeValue = "65", frequency = BudgetFrequency.MONTHLY)
        val data2 = testBudgetResponseData(budgetId = 101, type = BudgetType.MERCHANT, typeValue = "65", frequency = BudgetFrequency.FORTNIGHTLY)
        val data3 = testBudgetResponseData(budgetId = 102, type = BudgetType.MERCHANT, typeValue = "63", frequency = BudgetFrequency.MONTHLY)
        val data4 = testBudgetResponseData(budgetId = 103, type = BudgetType.MERCHANT, typeValue = "62", frequency = BudgetFrequency.DAILY)
        val data5 = testBudgetResponseData(budgetId = 104, type = BudgetType.MERCHANT, typeValue = "61", frequency = BudgetFrequency.ANNUALLY)
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchMerchantBudgets(65L, frequency = BudgetFrequency.MONTHLY).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(1, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchTransactionCategoryBudgets() {
        initSetup()

        val data1 = testBudgetResponseData(budgetId = 100, type = BudgetType.TRANSACTION_CATEGORY, typeValue = "65")
        val data2 = testBudgetResponseData(budgetId = 101, type = BudgetType.MERCHANT, typeValue = "65")
        val data3 = testBudgetResponseData(budgetId = 102, type = BudgetType.MERCHANT, typeValue = "63")
        val data4 = testBudgetResponseData(budgetId = 103, type = BudgetType.TRANSACTION_CATEGORY, typeValue = "62")
        val data5 = testBudgetResponseData(budgetId = 104, type = BudgetType.MERCHANT, typeValue = "61")
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchTransactionCategoryBudgets(65L).test()
        val testObserver2 = budgets.fetchTransactionCategoryBudgets().test()

        testObserver.awaitValue()
        testObserver2.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(1, testObserver.value().data?.size)
        assertEquals(2, testObserver2.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchTransactionCategoryBudgetsFiltered() {
        initSetup()

        val data1 = testBudgetResponseData(budgetId = 100, type = BudgetType.TRANSACTION_CATEGORY, typeValue = "65", frequency = BudgetFrequency.MONTHLY)
        val data2 = testBudgetResponseData(budgetId = 101, type = BudgetType.TRANSACTION_CATEGORY, typeValue = "65", frequency = BudgetFrequency.FORTNIGHTLY)
        val data3 = testBudgetResponseData(budgetId = 102, type = BudgetType.MERCHANT, typeValue = "63", frequency = BudgetFrequency.MONTHLY)
        val data4 = testBudgetResponseData(budgetId = 103, type = BudgetType.MERCHANT, typeValue = "62", frequency = BudgetFrequency.DAILY)
        val data5 = testBudgetResponseData(budgetId = 104, type = BudgetType.MERCHANT, typeValue = "61", frequency = BudgetFrequency.ANNUALLY)
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchTransactionCategoryBudgets(65L, frequency = BudgetFrequency.MONTHLY).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(1, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchBudgetCategoryBudgets() {
        initSetup()

        val data1 = testBudgetResponseData(budgetId = 100, type = BudgetType.BUDGET_CATEGORY, typeValue = "living")
        val data2 = testBudgetResponseData(budgetId = 101, type = BudgetType.BUDGET_CATEGORY, typeValue = "lifestyle")
        val data3 = testBudgetResponseData(budgetId = 102, type = BudgetType.MERCHANT, typeValue = "63")
        val data4 = testBudgetResponseData(budgetId = 103, type = BudgetType.MERCHANT, typeValue = "62")
        val data5 = testBudgetResponseData(budgetId = 104, type = BudgetType.BUDGET_CATEGORY, typeValue = "lifestyle")
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchBudgetCategoryBudgets(BudgetCategory.LIFESTYLE).test()
        val testObserver2 = budgets.fetchBudgetCategoryBudgets().test()

        testObserver.awaitValue()
        testObserver2.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(2, testObserver.value().data?.size)
        assertEquals(3, testObserver2.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchBudgetCategoryBudgetsFiltered() {
        initSetup()

        val data1 = testBudgetResponseData(budgetId = 100, type = BudgetType.BUDGET_CATEGORY, typeValue = "living", frequency = BudgetFrequency.MONTHLY)
        val data2 = testBudgetResponseData(budgetId = 101, type = BudgetType.BUDGET_CATEGORY, typeValue = "lifestyle", frequency = BudgetFrequency.FORTNIGHTLY)
        val data3 = testBudgetResponseData(budgetId = 102, type = BudgetType.MERCHANT, typeValue = "63", frequency = BudgetFrequency.MONTHLY)
        val data4 = testBudgetResponseData(budgetId = 103, type = BudgetType.BUDGET_CATEGORY, typeValue = "living", frequency = BudgetFrequency.DAILY)
        val data5 = testBudgetResponseData(budgetId = 104, type = BudgetType.MERCHANT, typeValue = "61", frequency = BudgetFrequency.ANNUALLY)
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchBudgetCategoryBudgets(BudgetCategory.LIVING, frequency = BudgetFrequency.MONTHLY).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(1, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchBudgets() {
        initSetup()

        val data1 = testBudgetResponseData(100, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data2 = testBudgetResponseData(101, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data3 = testBudgetResponseData(102, frequency = BudgetFrequency.ANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data4 = testBudgetResponseData(103, frequency = BudgetFrequency.ANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data5 = testBudgetResponseData(105, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data6 = testBudgetResponseData(106, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.CANCELLED, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data7 = testBudgetResponseData(107, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.ABOVE)
        val data8 = testBudgetResponseData(108, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data9 = testBudgetResponseData(109, frequency = BudgetFrequency.BIANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)

        val list = mutableListOf(data1, data2, data3, data4, data5, data6, data7, data8, data9)
        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchBudgets(
            frequency = BudgetFrequency.MONTHLY,
            status = BudgetStatus.ACTIVE,
            trackingStatus = BudgetTrackingStatus.EQUAL
        ).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().data?.isNotEmpty() == true)
        assertEquals(4, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchBudgetsWithRelation() {
        initSetup()

        val data1 = testBudgetResponseData(100, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data2 = testBudgetResponseData(101, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data3 = testBudgetResponseData(102, frequency = BudgetFrequency.ANNUALLY, status = BudgetStatus.UNSTARTED, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data4 = testBudgetResponseData(103, frequency = BudgetFrequency.ANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data5 = testBudgetResponseData(105, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data6 = testBudgetResponseData(106, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.CANCELLED, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data7 = testBudgetResponseData(107, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.ABOVE)
        val data8 = testBudgetResponseData(108, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data9 = testBudgetResponseData(109, frequency = BudgetFrequency.BIANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)

        val list = mutableListOf(data1, data2, data3, data4, data5, data6, data7, data8, data9)
        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val data11 = testBudgetPeriodResponseData(budgetPeriodId = 100, budgetId = 100, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data12 = testBudgetPeriodResponseData(budgetPeriodId = 101, budgetId = 101, trackingStatus = BudgetTrackingStatus.ABOVE)
        val data13 = testBudgetPeriodResponseData(budgetPeriodId = 102, budgetId = 103, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data14 = testBudgetPeriodResponseData(budgetPeriodId = 103, budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data15 = testBudgetPeriodResponseData(budgetPeriodId = 104, budgetId = 201, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data16 = testBudgetPeriodResponseData(budgetPeriodId = 105, budgetId = 100, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data17 = testBudgetPeriodResponseData(budgetPeriodId = 106, budgetId = 100, trackingStatus = BudgetTrackingStatus.EQUAL)
        val periods = mutableListOf(data11, data12, data13, data14, data15, data16, data17)

        database.budgetPeriods().insertAll(*periods.map { it.toBudgetPeriod() }.toList().toTypedArray())

        val testObserver = budgets.fetchBudgetsWithRelation(status = BudgetStatus.ACTIVE).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().data?.isNotEmpty() == true)
        assertEquals(7, testObserver.value().data?.size)
        assertEquals(testObserver.value().data?.get(0)?.budget?.budgetId, testObserver.value().data?.get(0)?.periods?.get(0)?.budgetId)
        assertEquals(testObserver.value().data?.get(0)?.budget?.budgetId, 100L)
        assertEquals(testObserver.value().data?.get(0)?.periods?.size, 3)

        tearDown()
    }

    @Test
    fun testRefreshBudgetsFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        budgets.refreshBudgets { result ->
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
    fun testRefreshBudgetsAll() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.budget_valid)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath?.contains(BudgetsAPI.URL_BUDGETS) == true) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudgets { resource ->
            val testObserver = budgets.fetchBudgets(current = true).test()
            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(3, testObserver.value().data?.size)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(BudgetsAPI.URL_BUDGETS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshBudgetsFiltered() {
        initSetup()

        val signal = CountDownLatch(1)

        val requestPath = "budgets?current=true"

        val body = readStringFromJson(app, R.raw.budget_valid)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudgets(true) { resource ->
            val testObserver = budgets.fetchBudgets(current = true).test()
            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(3, testObserver.value().data?.size)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshBudgetsFailedIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        budgets.refreshBudgets(true) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchBudgetById() {
        initSetup()

        val data1 = testBudgetResponseData(100, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data2 = testBudgetResponseData(101, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data3 = testBudgetResponseData(102, frequency = BudgetFrequency.ANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data4 = testBudgetResponseData(103, frequency = BudgetFrequency.ANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data5 = testBudgetResponseData(105, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data6 = testBudgetResponseData(106, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.CANCELLED, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data7 = testBudgetResponseData(107, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.ABOVE)
        val data8 = testBudgetResponseData(108, frequency = BudgetFrequency.MONTHLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data9 = testBudgetResponseData(109, frequency = BudgetFrequency.BIANNUALLY, status = BudgetStatus.ACTIVE, trackingStatus = BudgetTrackingStatus.EQUAL)

        val list = mutableListOf(data1, data2, data3, data4, data5, data6, data7, data8, data9)
        database.budgets().insertAll(*list.map { it.toBudget() }.toList().toTypedArray())

        val testObserver = budgets.fetchBudget(100).test()

        testObserver.awaitValue()
        assertEquals(100L, testObserver.value().data?.budgetId)
        assertEquals(BudgetFrequency.MONTHLY, testObserver.value().data?.frequency)

        tearDown()
    }

    @Test
    fun testRefreshBudgetById() {
        initSetup()

        val budgetId: Long = 6
        val signal = CountDownLatch(1)
        val requestPath = "budgets/$budgetId"

        val body = readStringFromJson(app, R.raw.budget_id_6)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudget(budgetId) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudget(budgetId).test()

            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(budgetId, testObserver.value().data?.budgetId)
            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshBudgetByIdFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)

        clearLoggedInPreferences()

        budgets.refreshBudget(3211) { result ->
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
    fun testCreateBudgetCategoryBudget() {
        initSetup()

        val signal = CountDownLatch(1)
        val body = readStringFromJson(app, R.raw.budget_id_6)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == BudgetsAPI.URL_BUDGETS) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.createBudgetCategoryBudget(
            BudgetFrequency.MONTHLY, BigDecimal(1000), BudgetCategory.LIVING, null,
            "https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg"
        ) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudget(6).test()

            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(6L, testObserver.value().data?.budgetId)
            assertEquals(BudgetFrequency.MONTHLY, testObserver.value().data?.frequency)
            assertEquals(BudgetCategory.LIVING, testObserver.value().data?.typeValue?.toBudgetCategory())

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(BudgetsAPI.URL_BUDGETS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testCreateMerchantCategoryBudget() {
        initSetup()

        val signal = CountDownLatch(1)
        val body = readStringFromJson(app, R.raw.budget_id_7)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == BudgetsAPI.URL_BUDGETS) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.createMerchantBudget(
            BudgetFrequency.MONTHLY, BigDecimal(1000), 7, null,
            "https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg"
        ) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudget(7).test()

            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(7L, testObserver.value().data?.budgetId)
            assertEquals(BudgetType.MERCHANT, testObserver.value().data?.type)
            assertEquals("7", testObserver.value().data?.typeValue)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(BudgetsAPI.URL_BUDGETS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testCreateTransactionCategoryBudget() {
        initSetup()

        val signal = CountDownLatch(1)
        val body = readStringFromJson(app, R.raw.budget_id_8)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == BudgetsAPI.URL_BUDGETS) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.createCategoryBudget(
            BudgetFrequency.MONTHLY, BigDecimal(1000), 8L, null,
            "https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg"
        ) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudget(8).test()

            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(8L, testObserver.value().data?.budgetId)
            assertEquals(BudgetType.TRANSACTION_CATEGORY, testObserver.value().data?.type)
            assertEquals("8", testObserver.value().data?.typeValue)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(BudgetsAPI.URL_BUDGETS, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testCreateBudgetFailsIfLoggedOut() {
        initSetup()

        val signal = CountDownLatch(1)
        clearLoggedInPreferences()

        budgets.createBudgetCategoryBudget(
            BudgetFrequency.MONTHLY, BigDecimal(1000), BudgetCategory.LIFESTYLE, null,
            "https://helpx.adobe.com/content/dam/help/en/stock/how-to/visual-reverse-image-search/jcr_content/main-pars/image/visual-reverse-image-search-v2_intro.jpg"
        ) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateBudget() {
        initSetup()

        val signal = CountDownLatch(1)
        val budgetId: Long = 6

        val requestPath = "budgets/$budgetId"

        val body = readStringFromJson(app, R.raw.budget_id_6)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        val budget = testBudgetResponseData(budgetId, status = BudgetStatus.UNSTARTED, trackingStatus = BudgetTrackingStatus.EQUAL).toBudget()

        database.budgets().insert(budget)

        budgets.updateBudget(budget) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudget(budgetId).test()

            testObserver.awaitValue()
            val models = testObserver.value().data
            assertNotNull(models)
            assertEquals(budgetId, models?.budgetId)
            assertEquals(BudgetTrackingStatus.BELOW, models?.trackingStatus)
            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testUpdateBudgetFailsIfLoggedOut() {
        initSetup()

        clearLoggedInPreferences()
        val signal = CountDownLatch(1)

        val budget = testBudgetResponseData(6).toBudget()
        budgets.updateBudget(budget) { result ->
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
    fun testDeleteBudget() {
        initSetup()

        val signal = CountDownLatch(1)
        val budgetId: Long = 6

        val requestPath = "budgets/$budgetId"

        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(204)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        database.budgets().insert(testBudgetResponseData(budgetId).toBudget())

        var testObserver = budgets.fetchBudget(budgetId).test()

        testObserver.awaitValue()
        val model = testObserver.value().data
        assertNotNull(model)
        assertEquals(budgetId, model?.budgetId)

        budgets.deleteBudget(budgetId) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            testObserver = budgets.fetchBudget(budgetId).test()

            testObserver.awaitValue()
            assertNull(testObserver.value().data)
            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testDeleteBudgetFailsIfLoggedOut() {
        initSetup()

        clearLoggedInPreferences()
        val signal = CountDownLatch(1)

        budgets.deleteBudget(6) { result ->
            assertEquals(Result.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertEquals(DataErrorType.AUTHENTICATION, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)
            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    // Budget Period Tests

    @Test
    fun testFetchBudgetPeriodById() {
        initSetup()

        val data1 = testBudgetPeriodResponseData(budgetPeriodId = 100)
        val data2 = testBudgetPeriodResponseData(budgetPeriodId = 101)
        val data3 = testBudgetPeriodResponseData(budgetPeriodId = 102)
        val data4 = testBudgetPeriodResponseData(budgetPeriodId = 103)
        val data5 = testBudgetPeriodResponseData(budgetPeriodId = 104)
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgetPeriods().insertAll(*list.map { it.toBudgetPeriod() }.toList().toTypedArray())

        val testObserver = budgets.fetchBudgetPeriod(data3.budgetPeriodId).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(data3.budgetPeriodId, testObserver.value().data?.budgetPeriodId)

        tearDown()
    }

    @Test
    fun testFetchBudgetPeriods() {
        initSetup()

        val data1 = testBudgetPeriodResponseData(budgetPeriodId = 100, budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data2 = testBudgetPeriodResponseData(budgetPeriodId = 101, budgetId = 200, trackingStatus = BudgetTrackingStatus.ABOVE)
        val data3 = testBudgetPeriodResponseData(budgetPeriodId = 102, budgetId = 201, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data4 = testBudgetPeriodResponseData(budgetPeriodId = 103, budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data5 = testBudgetPeriodResponseData(budgetPeriodId = 104, budgetId = 201, trackingStatus = BudgetTrackingStatus.EQUAL)
        val list = mutableListOf(data1, data2, data3, data4, data5)

        database.budgetPeriods().insertAll(*list.map { it.toBudgetPeriod() }.toList().toTypedArray())

        val testObserver = budgets.fetchBudgetPeriods(budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(2, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchBudgetByIdWithRelation() {
        initSetup()

        database.budgets().insert(testBudgetResponseData(budgetId = 6).toBudget())
        database.budgetPeriods().insert(testBudgetPeriodResponseData(budgetPeriodId = 456, budgetId = 6).toBudgetPeriod())

        val testObserver = budgets.fetchBudgetWithRelation(6).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(456L, testObserver.value().data?.periods?.get(0)?.budgetPeriodId)
        assertEquals(6L, testObserver.value().data?.budget?.budgetId)

        tearDown()
    }

    @Test
    fun testRefreshBudgetPeriods() {
        initSetup()

        val signal = CountDownLatch(1)
        val budgetId: Long = 6
        val requestPath = "budgets/$budgetId/periods"

        val body = readStringFromJson(app, R.raw.budget_periods_daily)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudgetPeriods(budgetId = budgetId) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudgetPeriods(budgetId = budgetId).test()

            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(15, testObserver.value().data?.size)

            val period = testObserver.value().data?.first()
            assertEquals(85L, period?.budgetPeriodId)
            assertEquals(6L, period?.budgetId)
            assertEquals(BigDecimal("111.42"), period?.currentAmount)
            assertEquals("2019-11-22", period?.endDate)
            assertEquals(BigDecimal("173.5"), period?.requiredAmount)
            assertEquals("2019-11-21", period?.startDate)
            assertEquals(BigDecimal("15.62"), period?.targetAmount)
            assertEquals(BudgetTrackingStatus.BELOW, period?.trackingStatus)
            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshBudgetPeriodsFailsIfLoggedOut() {
        initSetup()

        clearLoggedInPreferences()
        val signal = CountDownLatch(1)

        budgets.refreshBudgetPeriods(budgetId = 6) { result ->
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
    fun testRefreshBudgetPeriodById() {
        initSetup()

        val signal = CountDownLatch(1)
        val budgetId: Long = 6
        val periodId: Long = 85
        val requestPath = "budgets/$budgetId/periods/$periodId"

        val body = readStringFromJson(app, R.raw.budget_period_id_85)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudgetPeriod(budgetId = budgetId, periodId = periodId) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudgetPeriod(periodId).test()

            testObserver.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(85L, testObserver.value().data?.budgetPeriodId)
            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshBudgetPeriodByIdFailsIfLoggedOut() {
        initSetup()
        val signal = CountDownLatch(1)
        clearLoggedInPreferences()

        budgets.refreshBudgetPeriod(budgetId = 6, periodId = 85) { result ->
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
    fun testBudgetPeriodsLinkToBudgets() {
        initSetup()

        val signal = CountDownLatch(2)
        val budgetId: Long = 6
        val requestPath = "budgets/$budgetId/periods"

        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(readStringFromJson(app, R.raw.budget_periods_daily))
                } else if (request?.trimmedPath == BudgetsAPI.URL_BUDGETS) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(readStringFromJson(app, R.raw.budget_valid))
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudgets { resource ->
            assertEquals(Result.Status.SUCCESS, resource.status)
            assertNull(resource.error)
            signal.countDown()
        }

        budgets.refreshBudgetPeriods(budgetId) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)
            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        val testObserver = budgets.fetchBudgetWithRelation(6).test()

        testObserver.awaitValue()
        val model = testObserver.value().data
        assertNotNull(model)
        assertEquals(6L, model?.budget?.budgetId)
        assertEquals(model?.periods?.get(0)?.budgetPeriodId, 85L)

        tearDown()
    }

    @Test
    fun testRefreshBudgetPeriodsUpdatesExistingWithToAndFrom() {
        initSetup()

        // testLinkingRemoveCachedCascade for budget periods as well
        val signal = CountDownLatch(1)
        val budgetId: Long = 6
        val periodId: Long = 85
        val requestPath = "budgets/$budgetId/periods?from_date=2019-11-20&to_date=2019-11-23"

        val data11 = testBudgetPeriodResponseData(budgetPeriodId = 85, budgetId = 100, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data12 = testBudgetPeriodResponseData(budgetPeriodId = 101, budgetId = 6, trackingStatus = BudgetTrackingStatus.ABOVE)
        // below line tests removing of cached budget periods
        val data13 = testBudgetPeriodResponseData(budgetPeriodId = 102, budgetId = 6, trackingStatus = BudgetTrackingStatus.EQUAL, fromDate = "2019-11-21", toDate = "2019-11-21")
        val data14 = testBudgetPeriodResponseData(budgetPeriodId = 103, budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data15 = testBudgetPeriodResponseData(budgetPeriodId = 104, budgetId = 201, trackingStatus = BudgetTrackingStatus.EQUAL)
        val periods = mutableListOf(data11, data12, data13, data14, data15)

        database.budgetPeriods().insertAll(*periods.map { it.toBudgetPeriod() }.toList().toTypedArray())

        val body = readStringFromJson(app, R.raw.budget_periods_daily)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        val testObserverX = budgets.fetchBudgetPeriod(periodId).test()
        testObserverX.awaitValue()
        assertEquals(100L, testObserverX.value().data?.budgetId) // initially 100

        budgets.refreshBudgetPeriods(budgetId = budgetId, fromDate = "2019-11-20", toDate = "2019-11-23") { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudgetPeriod(periodId).test()
            val testObserver2 = budgets.fetchBudgetPeriods().test()
            val testObserver3 = budgets.fetchBudgetPeriod(102).test()

            testObserver.awaitValue()
            testObserver2.awaitValue()
            testObserver3.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(6L, testObserver.value().data?.budgetId) // changed from 100 to 6
            assertEquals(18, testObserver2.value().data?.size) // 15 from api + 5existing -1 in from & to date removed and -1 budgetPeriodId 85 updated
            assertNull(testObserver3.value().data)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshBudgetPeriodsUpdatesExisting() {
        initSetup()

        val signal = CountDownLatch(1)
        val budgetId: Long = 6
        val periodId: Long = 85
        val requestPath = "budgets/$budgetId/periods"

        val data11 = testBudgetPeriodResponseData(budgetPeriodId = 85, budgetId = 100, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data12 = testBudgetPeriodResponseData(budgetPeriodId = 101, budgetId = 6, trackingStatus = BudgetTrackingStatus.ABOVE)
        // below line tests removing of cached budget periods
        val data13 = testBudgetPeriodResponseData(budgetPeriodId = 102, budgetId = 6, trackingStatus = BudgetTrackingStatus.EQUAL, fromDate = "2019-11-21", toDate = "2019-11-21")
        val data14 = testBudgetPeriodResponseData(budgetPeriodId = 103, budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data15 = testBudgetPeriodResponseData(budgetPeriodId = 104, budgetId = 201, trackingStatus = BudgetTrackingStatus.EQUAL)
        val periods = mutableListOf(data11, data12, data13, data14, data15)

        database.budgetPeriods().insertAll(*periods.map { it.toBudgetPeriod() }.toList().toTypedArray())

        val body = readStringFromJson(app, R.raw.budget_periods_daily)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        val testObserverX = budgets.fetchBudgetPeriod(periodId).test()
        testObserverX.awaitValue()
        assertEquals(100L, testObserverX.value().data?.budgetId) // initially 100

        budgets.refreshBudgetPeriods(budgetId = budgetId) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = budgets.fetchBudgetPeriod(periodId).test()
            val testObserver2 = budgets.fetchBudgetPeriods().test()
            val testObserver3 = budgets.fetchBudgetPeriod(102).test()

            testObserver.awaitValue()
            testObserver2.awaitValue()
            testObserver3.awaitValue()
            assertNotNull(testObserver.value().data)
            assertEquals(6L, testObserver.value().data?.budgetId) // changed from 100 to 6
            assertEquals(17, testObserver2.value().data?.size) // 15 from api + 5existing -2 as only 2 with this budgetId in database -1 as 85 is common
            assertNull(testObserver3.value().data)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRemoveCachedBudgets() {
        initSetup()

        val signal = CountDownLatch(1)

        val requestPath = "budgets"

        val data11 = testBudgetResponseData(budgetId = 85, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data12 = testBudgetResponseData(budgetId = 6, trackingStatus = BudgetTrackingStatus.ABOVE)
        // below line tests removing of cached budget periods
        val data14 = testBudgetResponseData(budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL)
        val data15 = testBudgetResponseData(budgetId = 201, trackingStatus = BudgetTrackingStatus.EQUAL)
        val periods = mutableListOf(data11, data12, data14, data15)

        database.budgets().insertAll(*periods.map { it.toBudget() }.toList().toTypedArray())

        val body = readStringFromJson(app, R.raw.budget_valid)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudgets { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver2 = budgets.fetchBudgets().test()
            testObserver2.awaitValue()
            assertEquals(3, testObserver2.value().data?.size) // 3 from api

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRemoveCachedBudgetsWithBudgetType() {
        initSetup()

        val signal = CountDownLatch(1)

        val requestPath = "budgets?category_type=budget_category"

        val data11 = testBudgetResponseData(budgetId = 85, trackingStatus = BudgetTrackingStatus.EQUAL, type = BudgetType.BUDGET_CATEGORY)
        val data12 = testBudgetResponseData(budgetId = 6, trackingStatus = BudgetTrackingStatus.ABOVE, type = BudgetType.BUDGET_CATEGORY)
        // below line tests removing of cached budget periods
        val data14 = testBudgetResponseData(budgetId = 200, trackingStatus = BudgetTrackingStatus.EQUAL, type = BudgetType.BUDGET_CATEGORY)
        val data15 = testBudgetResponseData(budgetId = 201, trackingStatus = BudgetTrackingStatus.EQUAL, type = BudgetType.MERCHANT)
        val periods = mutableListOf(data11, data12, data14, data15)

        database.budgets().insertAll(*periods.map { it.toBudget() }.toList().toTypedArray())

        val body = readStringFromJson(app, R.raw.budget_valid)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == requestPath) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        budgets.refreshBudgets(budgetType = BudgetType.BUDGET_CATEGORY) { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver2 = budgets.fetchBudgets().test()
            testObserver2.awaitValue()
            assertEquals(4, testObserver2.value().data?.size) // 3 from api + 1 cached merchant

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testLinkingRemoveCachedCascade() {
        initSetup()

        val signal = CountDownLatch(1)

        val body = readStringFromJson(app, R.raw.budget_valid)
        mockServer.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.trimmedPath == BudgetsAPI.URL_BUDGETS) {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        database.budgets().insert(testBudgetResponseData(budgetId = 7).toBudget())
        database.budgetPeriods().insert(testBudgetPeriodResponseData(budgetPeriodId = 456, budgetId = 7).toBudgetPeriod())
        database.budgetPeriods().insert(testBudgetPeriodResponseData(budgetPeriodId = 457, budgetId = 7).toBudgetPeriod())

        budgets.fetchBudget(budgetId = 7).test().apply {
            awaitValue()

            assertEquals(7L, value().data?.budgetId)
        }

        budgets.fetchBudgetPeriods(budgetId = 7).test().apply {
            awaitValue()

            assertEquals(2, value().data?.size)
            assertEquals(456L, value().data?.get(0)?.budgetPeriodId)
            assertEquals(457L, value().data?.get(1)?.budgetPeriodId)
        }

        budgets.refreshBudgets { resource ->
            assertEquals(Result.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            budgets.fetchBudgets().test().apply {
                awaitValue()
                assertNotNull(value().data)
                assertEquals(3, value().data?.size)
            }

            budgets.fetchBudget(budgetId = 7).test().apply {
                awaitValue()
                assertNull(value().data)
            }

            budgets.fetchBudgetPeriods(7L).test().apply {
                awaitValue()
                assertEquals(0, value().data?.size)
            }

            budgets.fetchBudgetPeriod(budgetPeriodId = 456).test().apply {
                awaitValue()
                assertNull(value().data)
            }

            budgets.fetchBudgetPeriod(budgetPeriodId = 457).test().apply {
                awaitValue()
                assertNull(value().data)
            }

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }
}
