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
import us.frollo.frollosdk.mapping.toProvider
import us.frollo.frollosdk.mapping.toProviderAccount
import us.frollo.frollosdk.model.testProviderAccountResponseData
import us.frollo.frollosdk.model.testProviderResponseData

class ProviderDaoTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private val app =
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
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
    fun testLoadAll() {
        val data1 = testProviderResponseData(providerId = 1)
        val data2 = testProviderResponseData(providerId = 2)
        val data3 = testProviderResponseData(providerId = 3)
        val data4 = testProviderResponseData(providerId = 4)
        val list = mutableListOf(data1, data2, data3, data4)

        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        val testObserver = db.providers().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(4, testObserver.value().size)
    }

    @Test
    fun testLoadByProviderId() {
        val data = testProviderResponseData(providerId = 102)
        val list = mutableListOf(
            testProviderResponseData(providerId = 101),
            data,
            testProviderResponseData(providerId = 103)
        )
        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        val testObserver = db.providers().load(data.providerId).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(data.providerId, testObserver.value()?.providerId)
    }

    @Test
    fun testInsertAll() {
        val data1 = testProviderResponseData(providerId = 1)
        val data2 = testProviderResponseData(providerId = 2)
        val data3 = testProviderResponseData(providerId = 3)
        val list = mutableListOf(data1, data2, data3)

        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        val testObserver = db.providers().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testInsert() {
        val data = testProviderResponseData()

        db.providers().insert(data.toProvider())

        val testObserver = db.providers().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(data.providerId, testObserver.value()[0].providerId)
    }

    @Test
    fun testGetIds() {
        val data1 = testProviderResponseData(providerId = 100)
        val data2 = testProviderResponseData(providerId = 101)
        val data3 = testProviderResponseData(providerId = 102)
        val data4 = testProviderResponseData(providerId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        val ids = db.providers().getIds().sorted()
        assertEquals(4, ids.size)
        assertTrue(ids.containsAll(mutableListOf<Long>(100, 101, 102, 103)))
    }

    @Test
    fun testDeleteMany() {
        val data1 = testProviderResponseData(providerId = 100)
        val data2 = testProviderResponseData(providerId = 101)
        val data3 = testProviderResponseData(providerId = 102)
        val data4 = testProviderResponseData(providerId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        db.providers().deleteMany(longArrayOf(100, 103))

        val testObserver = db.providers().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testDelete() {
        val data1 = testProviderResponseData(providerId = 100)
        val data2 = testProviderResponseData(providerId = 101)
        val data3 = testProviderResponseData(providerId = 102)
        val data4 = testProviderResponseData(providerId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        db.providers().delete(100)

        val testObserver = db.providers().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testClear() {
        val data1 = testProviderResponseData(providerId = 100)
        val data2 = testProviderResponseData(providerId = 101)
        val data3 = testProviderResponseData(providerId = 102)
        val data4 = testProviderResponseData(providerId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        db.providers().clear()

        val testObserver = db.providers().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isEmpty())
    }

    @Test
    fun testLoadAllWithRelation() {
        db.providers().insert(testProviderResponseData(providerId = 123).toProvider())
        db.providerAccounts().insert(
            testProviderAccountResponseData(
                providerAccountId = 234,
                providerId = 123
            ).toProviderAccount()
        )
        db.providerAccounts().insert(
            testProviderAccountResponseData(
                providerAccountId = 235,
                providerId = 123
            ).toProviderAccount()
        )

        val testObserver = db.providers().loadWithRelation().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(1, testObserver.value().size)

        val model = testObserver.value()[0]

        assertEquals(123L, model.provider?.providerId)
        assertEquals(2, model.providerAccounts?.size)
        assertEquals(234L, model.providerAccounts?.get(0)?.providerAccountId)
        assertEquals(235L, model.providerAccounts?.get(1)?.providerAccountId)
    }

    @Test
    fun testLoadByProviderIdWithRelation() {
        db.providers().insert(testProviderResponseData(providerId = 123).toProvider())
        db.providerAccounts().insert(
            testProviderAccountResponseData(
                providerAccountId = 234,
                providerId = 123
            ).toProviderAccount()
        )
        db.providerAccounts().insert(
            testProviderAccountResponseData(
                providerAccountId = 235,
                providerId = 123
            ).toProviderAccount()
        )

        val testObserver = db.providers().loadWithRelation(providerId = 123).test()
        testObserver.awaitValue()

        val model = testObserver.value()

        assertEquals(123L, model?.provider?.providerId)
        assertEquals(2, model?.providerAccounts?.size)
        assertEquals(234L, model?.providerAccounts?.get(0)?.providerAccountId)
        assertEquals(235L, model?.providerAccounts?.get(1)?.providerAccountId)
    }

    @Test
    fun testFetchProvidersByIdsWithRelation() {
        val data1 = testProviderResponseData(providerId = 100)
        val data2 = testProviderResponseData(providerId = 101)
        val data3 = testProviderResponseData(providerId = 102)
        val data4 = testProviderResponseData(providerId = 103)
        val list = mutableListOf(data1, data2, data3, data4)
        db.providers().insertAll(*list.map { it.toProvider() }.toList().toTypedArray())

        val testObserver = db.providers().fetchProvidersByIdsWithRelation(listOf(100L, 103L).toLongArray()).test()
        testObserver.awaitValue()

        val model = testObserver.value()
        assertEquals(2, model?.size)
        assertEquals(100L, model?.get(0)?.provider?.providerId)
        assertEquals(1234L, model?.get(0)?.provider?.associatedProviderIds?.get(0))
    }
}
