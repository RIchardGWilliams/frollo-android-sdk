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

package us.frollo.frollosdk.database.dao

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jraska.livedata.test
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.mapping.toServiceOutage
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutageType
import us.frollo.frollosdk.model.testServiceOutageResponseData

class ServiceOutageDaoTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    private val db = SDKDatabase.getInstance(app)

    @Before
    fun setUp() {
        AndroidThreeTen.init(app)
    }

    @After
    fun tearDown() {
        db.clearAllTables()
    }

    @Test
    fun testLoadAndInsertAll() {
        val data1 = testServiceOutageResponseData().toServiceOutage()
        val data2 = testServiceOutageResponseData().toServiceOutage()
        val data3 = testServiceOutageResponseData().toServiceOutage()

        val list = mutableListOf(data1, data2, data3)

        db.serviceOutages().insertAll(*list.toTypedArray())

        val testObserver = db.serviceOutages().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testLoadByQuery() {
        val data1 = testServiceOutageResponseData(type = ServiceOutageType.INFO).toServiceOutage()
        val data2 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE).toServiceOutage()
        val data3 = testServiceOutageResponseData(type = ServiceOutageType.WARNING).toServiceOutage()
        val data4 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE).toServiceOutage()

        val list = mutableListOf(data1, data2, data3, data4)

        db.serviceOutages().insertAll(*list.toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM service_outage WHERE type == 'OUTAGE'")

        val testObserver = db.serviceOutages().loadByQuery(query).test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testFindByQuery() {
        val data1 = testServiceOutageResponseData(type = ServiceOutageType.INFO).toServiceOutage()
        val data2 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE, startDate = "2020-11-05T10:15:30+01:00").toServiceOutage()
        val data3 = testServiceOutageResponseData(type = ServiceOutageType.WARNING).toServiceOutage()
        val data4 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE, startDate = "2021-08-04T10:15:30+01:00").toServiceOutage()

        val list = mutableListOf(data1, data2, data3, data4)

        db.serviceOutages().insertAll(*list.toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM service_outage WHERE type == 'OUTAGE' LIMIT 1")

        val outages = db.serviceOutages().find(query)
        assertEquals(1, outages.size)
        assertEquals(ServiceOutageType.OUTAGE, outages.firstOrNull()?.type)
        assertEquals("2020-11-05T10:15:30+01:00", outages.firstOrNull()?.startDate)
    }

    @Test
    fun markOutageAsRead() {
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

        db.serviceOutages().insertAll(*list.toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM service_outage WHERE outage_id == 124")

        var outages = db.serviceOutages().find(query)
        assertEquals(1, outages.size)
        assertEquals(false, outages.firstOrNull()?.read)

        db.serviceOutages().markOutageAsRead(124)

        outages = db.serviceOutages().find(query)
        assertEquals(1, outages.size)
        assertEquals(true, outages.firstOrNull()?.read)
    }

    @Test
    fun testUpdate() {
        val data1 = testServiceOutageResponseData(type = ServiceOutageType.INFO).toServiceOutage().apply {
            outageId = 123
        }
        val data2 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE).toServiceOutage().apply {
            outageId = 124
        }
        val data3 = testServiceOutageResponseData(type = ServiceOutageType.WARNING).toServiceOutage().apply {
            outageId = 125
        }

        var list = mutableListOf(data1, data2, data3)

        db.serviceOutages().insertAll(*list.toTypedArray())

        data1.type = ServiceOutageType.WARNING
        data3.type = ServiceOutageType.WARNING

        list = mutableListOf(data1, data2)

        db.serviceOutages().update(*list.toTypedArray())

        val testObserver = db.serviceOutages().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(3, testObserver.value().size)
        assertEquals(123, testObserver.value()[0].outageId)
        assertEquals(ServiceOutageType.WARNING, testObserver.value()[0].type)
        assertEquals(124, testObserver.value()[1].outageId)
        assertEquals(ServiceOutageType.OUTAGE, testObserver.value()[1].type)
        assertEquals(125, testObserver.value()[2].outageId)
        assertEquals(ServiceOutageType.WARNING, testObserver.value()[2].type)
    }

    @Test
    fun testGetIds() {
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

        db.serviceOutages().insertAll(*list.toTypedArray())

        val ids = db.serviceOutages().getIds()
        assertEquals(3, ids.size)
    }

    @Test
    fun testDeleteMany() {
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

        db.serviceOutages().insertAll(*list.toTypedArray())

        db.serviceOutages().deleteMany(longArrayOf(123, 124))

        val testObserver = db.serviceOutages().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(1, testObserver.value().size)
    }

    @Test
    fun testClear() {
        val data1 = testServiceOutageResponseData(type = ServiceOutageType.INFO).toServiceOutage()
        val data2 = testServiceOutageResponseData(type = ServiceOutageType.OUTAGE).toServiceOutage()
        val data3 = testServiceOutageResponseData(type = ServiceOutageType.WARNING).toServiceOutage()

        val list = mutableListOf(data1, data2, data3)

        db.serviceOutages().insertAll(*list.toTypedArray())

        db.serviceOutages().clear()

        val testObserver = db.serviceOutages().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isEmpty())
    }
}
