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
import us.frollo.frollosdk.core.testSDKConfig
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.model.testFeatureConfigData

class FeatureConfigDaoTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

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
    fun testLoadAndInsertAll() {
        val data1 = testFeatureConfigData()
        val data2 = testFeatureConfigData()
        val data3 = testFeatureConfigData()

        val list = mutableListOf(data1, data2, data3)

        db.featureConfig().insertAll(*list.toTypedArray())

        val testObserver = db.featureConfig().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testLoadByQuery() {
        val data1 = testFeatureConfigData()
        val data2 = testFeatureConfigData()

        val list = mutableListOf(data1, data2)

        db.featureConfig().insertAll(*list.toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM feature_config")

        val testObserver = db.featureConfig().loadByQuery(query).test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testGetStaleKeys() {
        val data1 = testFeatureConfigData(key = "budgets")
        val data2 = testFeatureConfigData(key = "key1")
        val data3 = testFeatureConfigData(key = "key2")
        val list = mutableListOf(data1, data2, data3)

        db.featureConfig().insertAll(*list.toTypedArray())

        val staleKeys = db.featureConfig().getStaleKeys(arrayOf("budgets"))

        assertEquals(2, staleKeys.size)
        assertTrue(staleKeys.containsAll(listOf("key1", "key2")))
    }

    @Test
    fun testDeleteMany() {
        val data1 = testFeatureConfigData(key = "budgets")
        val data2 = testFeatureConfigData(key = "financial passport")
        val data3 = testFeatureConfigData(key = "key1")

        val list = mutableListOf(data1, data2, data3)

        db.featureConfig().insertAll(*list.toTypedArray())

        db.featureConfig().deleteMany(arrayOf("budgets", "financial passport"))

        val testObserver = db.featureConfig().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(1, testObserver.value().size)
        assertEquals("key1", testObserver.value()[0].key)
    }

    @Test
    fun testClear() {
        val data1 = testFeatureConfigData()
        val data2 = testFeatureConfigData()
        val data3 = testFeatureConfigData()

        val list = mutableListOf(data1, data2, data3)

        db.featureConfig().insertAll(*list.toTypedArray())

        db.featureConfig().clear()

        val testObserver = db.featureConfig().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isEmpty())
    }
}
