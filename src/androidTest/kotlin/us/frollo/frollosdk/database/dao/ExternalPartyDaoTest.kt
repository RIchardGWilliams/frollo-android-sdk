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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import us.frollo.frollosdk.core.testSDKConfig
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.mapping.toExternalParty
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyType
import us.frollo.frollosdk.model.testExternalPartyResponseData

class ExternalPartyDaoTest {

    @get:Rule val testRule = InstantTaskExecutorRule()

    private val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    private val db = SDKDatabase.getInstance(app, testSDKConfig())

    @Before
    fun setUp() {
        AndroidThreeTen.init(app)
    }

    @After
    fun tearDown() {
        db.clearAllTables()
    }

    @Test
    fun testLoadByPartytId() {
        val data = testExternalPartyResponseData(partyId = 102)
        val list = mutableListOf(testExternalPartyResponseData(partyId = 101), data, testExternalPartyResponseData(partyId = 103))
        db.externalParty().insertAll(*list.map { it.toExternalParty() }.toList().toTypedArray())

        val testObserver = db.externalParty().load(data.partyId).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(data.partyId, testObserver.value()?.partyId)
    }

    @Test
    fun testInsertAllAndLoadByQuery() {
        val data1 = testExternalPartyResponseData(partyId = 100, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data2 = testExternalPartyResponseData(partyId = 101, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data3 = testExternalPartyResponseData(partyId = 102, type = ExternalPartyType.CDR_INSIGHT)
        val data4 = testExternalPartyResponseData(partyId = 103, type = ExternalPartyType.TRUSTED_ADVISOR)
        val list = mutableListOf(data1, data2, data3, data4)

        db.externalParty().insertAll(*list.map { it.toExternalParty() }.toList().toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM external_party WHERE party_id IN (101,102,103)")
        val testObserver = db.externalParty().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testInsert() {
        val data = testExternalPartyResponseData()

        db.externalParty().insert(data.toExternalParty())

        val query = SimpleSQLiteQuery("SELECT * FROM external_party")
        val testObserver = db.externalParty().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(data.partyId, testObserver.value()[0].partyId)
    }

    @Test
    fun testGetIdsByQuery() {
        val data1 = testExternalPartyResponseData(partyId = 100, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data2 = testExternalPartyResponseData(partyId = 101, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data3 = testExternalPartyResponseData(partyId = 102, type = ExternalPartyType.CDR_INSIGHT)
        val data4 = testExternalPartyResponseData(partyId = 103, type = ExternalPartyType.TRUSTED_ADVISOR)
        val list = mutableListOf(data1, data2, data3, data4)

        db.externalParty().insertAll(*list.map { it.toExternalParty() }.toList().toTypedArray())

        val query = SimpleSQLiteQuery("SELECT party_id FROM external_party WHERE party_id IN (101,102)")
        val ids = db.externalParty().getIdsByQuery(query).sorted()

        assertEquals(2, ids.size)
        assertTrue(ids.containsAll(mutableListOf(101L, 102L)))
    }

    @Test
    fun testDeleteMany() {
        val data1 = testExternalPartyResponseData(partyId = 100, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data2 = testExternalPartyResponseData(partyId = 101, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data3 = testExternalPartyResponseData(partyId = 102, type = ExternalPartyType.CDR_INSIGHT)
        val data4 = testExternalPartyResponseData(partyId = 103, type = ExternalPartyType.TRUSTED_ADVISOR)
        val list = mutableListOf(data1, data2, data3, data4)

        db.externalParty().insertAll(*list.map { it.toExternalParty() }.toList().toTypedArray())

        db.externalParty().deleteMany(longArrayOf(100, 103))

        val query = SimpleSQLiteQuery("SELECT * FROM external_party")
        val testObserver = db.externalParty().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testClear() {
        val data1 = testExternalPartyResponseData(partyId = 100, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data2 = testExternalPartyResponseData(partyId = 101, type = ExternalPartyType.TRUSTED_ADVISOR)
        val data3 = testExternalPartyResponseData(partyId = 102, type = ExternalPartyType.CDR_INSIGHT)
        val data4 = testExternalPartyResponseData(partyId = 103, type = ExternalPartyType.TRUSTED_ADVISOR)
        val list = mutableListOf(data1, data2, data3, data4)

        db.externalParty().insertAll(*list.map { it.toExternalParty() }.toList().toTypedArray())

        db.externalParty().clear()

        val query = SimpleSQLiteQuery("SELECT * FROM external_party")
        val testObserver = db.externalParty().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isEmpty())
    }
}
