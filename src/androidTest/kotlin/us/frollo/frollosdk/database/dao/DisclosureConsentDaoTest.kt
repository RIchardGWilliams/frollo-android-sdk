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
import us.frollo.frollosdk.model.testDisclosureConsentResponseData

class DisclosureConsentDaoTest {

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
        val data = testDisclosureConsentResponseData(consentId = 34567)
        val list = mutableListOf(testDisclosureConsentResponseData(consentId = 12345), data, testDisclosureConsentResponseData(consentId = 56789))
        db.disclosureConsent().insertAll(*list.map { it.toDisclosureConsent() }.toList().toTypedArray())

        val testObserver = db.disclosureConsent().load(data.consentId).test()

        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(data.consentId, testObserver.value()?.consentId)
    }

    @Test
    fun testInsertAllAndLoadByQuery() {
        val data1 = testDisclosureConsentResponseData(consentId = 12345, status = ConsentStatus.WITHDRAWN)
        val data2 = testDisclosureConsentResponseData(consentId = 34567, status = ConsentStatus.ACTIVE)
        val data3 = testDisclosureConsentResponseData(consentId = 56789)
        val list = mutableListOf(data1, data2, data3)

        db.disclosureConsent().insertAll(*list.map { it.toDisclosureConsent() }.toList().toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM disclosure_consent WHERE consent_id IN (12345,34567)")
        val testObserver = db.disclosureConsent().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testInsert() {
        val data = testDisclosureConsentResponseData()

        db.disclosureConsent().insert(data.toDisclosureConsent())

        val query = SimpleSQLiteQuery("SELECT * FROM disclosure_consent")
        val testObserver = db.disclosureConsent().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(data.consentId, testObserver.value()[0].consentId)
    }

    @Test
    fun testGetIdsByQuery() {
        val data1 = testDisclosureConsentResponseData(consentId = 12345, status = ConsentStatus.WITHDRAWN)
        val data2 = testDisclosureConsentResponseData(consentId = 34567, status = ConsentStatus.ACTIVE)
        val data3 = testDisclosureConsentResponseData(consentId = 56789)
        val list = mutableListOf(data1, data2, data3)

        db.disclosureConsent().insertAll(*list.map { it.toDisclosureConsent() }.toList().toTypedArray())

        val query = SimpleSQLiteQuery("SELECT consent_id FROM disclosure_consent WHERE consent_id IN (12345,56789)")
        val ids = db.disclosureConsent().getIdsByQuery(query).sorted()

        assertEquals(2, ids.size)
        assertTrue(ids.containsAll(mutableListOf(12345L, 56789L)))
    }

    @Test
    fun testDeleteMany() {
        val data1 = testDisclosureConsentResponseData(consentId = 12345, status = ConsentStatus.WITHDRAWN)
        val data2 = testDisclosureConsentResponseData(consentId = 34567, status = ConsentStatus.ACTIVE)
        val data3 = testDisclosureConsentResponseData(consentId = 56789)
        val list = mutableListOf(data1, data2, data3)

        db.disclosureConsent().insertAll(*list.map { it.toDisclosureConsent() }.toList().toTypedArray())

        db.disclosureConsent().deleteMany(longArrayOf(12345, 56789))

        val query = SimpleSQLiteQuery("SELECT * FROM disclosure_consent")
        val testObserver = db.disclosureConsent().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(1, testObserver.value().size)
    }

    @Test
    fun testClear() {
        val data1 = testDisclosureConsentResponseData(consentId = 12345, status = ConsentStatus.WITHDRAWN)
        val data2 = testDisclosureConsentResponseData(consentId = 34567, status = ConsentStatus.ACTIVE)
        val data3 = testDisclosureConsentResponseData(consentId = 56789)
        val list = mutableListOf(data1, data2, data3)

        db.disclosureConsent().insertAll(*list.map { it.toDisclosureConsent() }.toList().toTypedArray())

        db.disclosureConsent().clear()

        val query = SimpleSQLiteQuery("SELECT * FROM disclosure_consent")
        val testObserver = db.disclosureConsent().loadByQuery(query).test()

        testObserver.awaitValue()
        assertTrue(testObserver.value().isEmpty())
    }
}
