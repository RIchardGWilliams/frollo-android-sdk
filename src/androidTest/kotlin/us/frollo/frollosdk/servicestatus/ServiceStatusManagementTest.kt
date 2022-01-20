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

package us.frollo.frollosdk.servicestatus

import androidx.sqlite.db.SimpleSQLiteQuery
import com.jraska.livedata.test
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import us.frollo.frollosdk.BaseAndroidTest
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.mapping.toServiceOutage
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutageType
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceStatusType
import us.frollo.frollosdk.model.testServiceOutageResponseData
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ServiceStatusManagementTest : BaseAndroidTest() {

    @Test
    fun testFetchServiceOutages() {
        initSetup()

        val data1 = testServiceOutageResponseData().toServiceOutage()
        val data2 = testServiceOutageResponseData().toServiceOutage()
        val data3 = testServiceOutageResponseData().toServiceOutage()

        val list = mutableListOf(data1, data2, data3)

        database.serviceOutages().insertAll(*list.toTypedArray())

        val testObserver = serviceStatusManagement.fetchServiceOutages().test()

        testObserver.awaitValue()
        assertTrue(testObserver.value()?.isNotEmpty() == true)
        assertEquals(3, testObserver.value()?.size)

        tearDown()
    }

    @Test
    fun testRefreshServiceOutages() {
        initSetup()

        val signal = CountDownLatch(1)

        val url = "api/v2/outage/frollo"

        val body = readStringFromJson(app, R.raw.service_outages)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == url) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        serviceStatusManagement.refreshServiceOutages(url, "frollo") { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = serviceStatusManagement.fetchServiceOutages().test()

            testObserver.awaitValue()
            val models = testObserver.value()
            assertNotNull(models)
            assertEquals(3, models?.size)
            assertEquals(86400L, models?.first()?.duration)
            assertEquals("2021-09-03T12:02:12.505+10:00", models?.first()?.startDate)
            assertEquals("2021-09-04T12:02:12.505+10:00", models?.first()?.endDate)
            assertEquals(ServiceOutageType.INFO, models?.first()?.type)
            assertEquals("VPS Information", models?.first()?.message?.title)
            assertEquals("Between Sat 19 June 12:30am-6:00am AEST, card features will be unavailable to use.", models?.first()?.message?.summary)
            assertEquals("Between Saturday 19 June 12:30am-6:00am AEST, scheduled maintenance will mean that you won't be able to use your Volt card. This includes activating your card, setting your PIN, making purchases, or withdrawing or checking your balance at ATMs.Once maintenance is complete, you'll be able to use all features as normal.", models?.first()?.message?.description)
            assertEquals("More info", models?.first()?.message?.actionName)
            assertEquals("https://www.voltbank.com.au/", models?.first()?.message?.actionUrl)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(url, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshServiceOutagesUpdatesExistingData() {
        initSetup()

        val signal = CountDownLatch(1)

        val url = "api/v2/outage/frollo"

        val body = readStringFromJson(app, R.raw.service_outages)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == url) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        val data1 = testServiceOutageResponseData(
            type = ServiceOutageType.INFO,
            startDate = "2021-09-03T12:02:12.505+10:00",
            endDate = "2021-09-04T12:02:12.505+10:00"
        ).toServiceOutage().apply {
            outageId = 123
        }
        val data2 = testServiceOutageResponseData(
            type = ServiceOutageType.OUTAGE,
            startDate = "2021-09-03T01:02:12.505+10:00",
            endDate = "2021-09-04T02:02:12.505+10:00"
        ).toServiceOutage().apply {
            outageId = 124
        }
        val data3 = testServiceOutageResponseData(
            type = ServiceOutageType.WARNING,
            startDate = "2021-09-03T12:02:12.505+10:00",
            endDate = "2021-09-04T12:02:12.505+10:00"
        ).toServiceOutage().apply {
            outageId = 125
        }

        val list = mutableListOf(data1, data2, data3)

        database.serviceOutages().insertAll(*list.toTypedArray())

        serviceStatusManagement.refreshServiceOutages(url, "frollo") { result ->
            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            val testObserver = serviceStatusManagement.fetchServiceOutages().test()

            testObserver.awaitValue()
            val models = testObserver.value()
            assertNotNull(models)
            assertEquals(3, models?.size)

            // Updated outages

            val model1 = models?.find {
                it.startDate == "2021-09-03T12:02:12.505+10:00" &&
                    it.endDate == "2021-09-04T12:02:12.505+10:00" &&
                    it.type == ServiceOutageType.INFO
            }
            assertEquals(123L, model1?.outageId)
            assertEquals("VPS Information", model1?.message?.title)

            val model2 = models?.find {
                it.startDate == "2021-09-03T12:02:12.505+10:00" &&
                    it.endDate == "2021-09-04T12:02:12.505+10:00" &&
                    it.type == ServiceOutageType.WARNING
            }
            assertEquals(125L, model2?.outageId)
            assertEquals("VPS Debit Cards Outage Planning", model2?.message?.title)

            // Inserted outage

            val model3 = models?.find {
                it.startDate == "2021-10-03T12:02:12.505+10:00" &&
                    it.endDate == "2021-10-04T12:02:12.505+10:00" &&
                    it.type == ServiceOutageType.OUTAGE
            }
            assertEquals("VPS Debit Cards Outage", model3?.message?.title)

            // Deleted outage

            val model4 = models?.find {
                it.startDate == "2021-09-03T01:02:12.505+10:00" &&
                    it.endDate == "2021-09-04T02:02:12.505+10:00" &&
                    it.type == ServiceOutageType.OUTAGE
            }
            assertNull(model4)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(url, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testMarkOutageAsRead() {
        initSetup()

        val signal = CountDownLatch(1)

        val data1 = testServiceOutageResponseData(type = ServiceOutageType.INFO).toServiceOutage().apply {
            outageId = 123
        }
        val data2 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE).toServiceOutage().apply {
            outageId = 124
        }
        val data3 = testServiceOutageResponseData(type = ServiceOutageType.WARNING).toServiceOutage().apply {
            outageId = 125
        }

        val list = mutableListOf(data1, data2, data3)

        database.serviceOutages().insertAll(*list.toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM service_outage WHERE outage_id == 124")

        val testObserver1 = serviceStatusManagement.fetchServiceOutages(query).test()

        testObserver1.awaitValue()
        assertTrue(testObserver1.value()?.isNotEmpty() == true)
        assertEquals(1, testObserver1.value()?.size)
        assertEquals(124L, testObserver1.value()?.first()?.outageId)
        assertEquals(false, testObserver1.value()?.first()?.read)

        serviceStatusManagement.markOutageAsRead(124) {
            val testObserver2 = serviceStatusManagement.fetchServiceOutages(query).test()

            testObserver2.awaitValue()
            assertTrue(testObserver2.value()?.isNotEmpty() == true)
            assertEquals(1, testObserver2.value()?.size)
            assertEquals(124L, testObserver2.value()?.first()?.outageId)
            assertEquals(true, testObserver2.value()?.first()?.read)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testFetchServiceStatus() {
        initSetup()

        val signal = CountDownLatch(1)

        val url = "api/v2/status/frollo"

        val body = readStringFromJson(app, R.raw.service_status)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == url) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        serviceStatusManagement.fetchServiceStatus(url, "frollo") { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            assertNull(resource.data?.duration)
            assertEquals("2021-09-03T12:02:12.505+10:00", resource.data?.startDate)
            assertEquals("2021-09-01T12:02:12.505+10:00", resource.data?.lastUpdated)
            assertNull(resource.data?.endDate)
            assertEquals(ServiceStatusType.INFO, resource.data?.status)
            assertEquals("Volt info We've scheduled in some maintenance", resource.data?.message?.title)
            assertEquals("Between Thu 8 Jul 4:00am-7:00am AEST, the app and card will be unavailable", resource.data?.message?.summary)
            assertEquals("Between Thursday 8 July 4:00am - 7:00am AEST, the app will be down while we make our updates. This also includes certain card features, which means you won't be able to activate your card, set your PIN, make purchases, or withdraw or check your balance at ATMs. Once maintenance is complete, you'll be able to use all features as normal.", resource.data?.message?.description)
            assertEquals("More info", resource.data?.message?.actionName)
            assertEquals("https://www.voltbank.com.au/", resource.data?.message?.actionUrl)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(url, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshServiceOutagesNoAuthHeaders() {
        initSetup()

        val url = "api/v2/outage/frollo"

        val body = readStringFromJson(app, R.raw.service_outages)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == url) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        serviceStatusManagement.refreshServiceOutages(url, "frollo")

        val request = mockServer.takeRequest()
        assertEquals(url, request.trimmedPath)

        assertEquals("frollo", request.getHeader("X-Host"))
        assertNull(request.getHeader("Authorization"))
        assertNull(request.getHeader("X-Api-Version"))
        assertNull(request.getHeader("X-Bundle-Id"))
        assertNull(request.getHeader("X-Device-Version"))
        assertNull(request.getHeader("X-Software-Version"))
        assertNull(request.getHeader("X-Session-Id"))

        tearDown()
    }

    @Test
    fun testRefreshServiceStatusNoAuthHeaders() {
        initSetup()

        val url = "api/v2/outage/frollo"

        val body = readStringFromJson(app, R.raw.service_status)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == url) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        serviceStatusManagement.fetchServiceStatus(url, "frollo") {}

        val request = mockServer.takeRequest()
        assertEquals(url, request.trimmedPath)

        assertEquals("frollo", request.getHeader("X-Host"))
        assertNull(request.getHeader("Authorization"))
        assertNull(request.getHeader("X-Api-Version"))
        assertNull(request.getHeader("X-Bundle-Id"))
        assertNull(request.getHeader("X-Device-Version"))
        assertNull(request.getHeader("X-Software-Version"))
        assertNull(request.getHeader("X-Session-Id"))

        tearDown()
    }
}
