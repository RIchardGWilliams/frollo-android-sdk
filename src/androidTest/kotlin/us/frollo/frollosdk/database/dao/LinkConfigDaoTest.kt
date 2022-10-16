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
import us.frollo.frollosdk.model.testLinkConfigData

class LinkConfigDaoTest {

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
        val data1 = testLinkConfigData()
        val data2 = testLinkConfigData()
        val data3 = testLinkConfigData()

        val list = mutableListOf(data1, data2, data3)

        db.linkConfig().insertAll(*list.toTypedArray())

        val testObserver = db.linkConfig().load().test()
        testObserver.awaitValue()
        Assert.assertTrue(testObserver.value().isNotEmpty())
        Assert.assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testLoadByQuery() {
        val data1 = testLinkConfigData()
        val data2 = testLinkConfigData()

        val list = mutableListOf(data1, data2)

        db.linkConfig().insertAll(*list.toTypedArray())

        val query = SimpleSQLiteQuery("SELECT * FROM link_config")

        val testObserver = db.linkConfig().loadByQuery(query).test()
        testObserver.awaitValue()
        Assert.assertTrue(testObserver.value().isNotEmpty())
        Assert.assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testGetStaleKeys() {
        val data1 = testLinkConfigData()
        val data2 = testLinkConfigData()
        val data3 = testLinkConfigData()
        val list = mutableListOf(data1, data2, data3)

        db.linkConfig().insertAll(*list.toTypedArray())

        val staleKeys = db.linkConfig().getStaleKeys(arrayOf("budgets"))

        Assert.assertEquals(1, staleKeys.size)
        Assert.assertTrue(staleKeys.containsAll(mutableListOf("budgets")))
    }

    @Test
    fun testDeleteMany() {
        val data1 = testLinkConfigData()
        val data2 = testLinkConfigData()
        val data3 = testLinkConfigData()

        val list = mutableListOf(data1, data2, data3)

        db.linkConfig().insertAll(*list.toTypedArray())

        db.linkConfig().deleteMany(arrayOf("terms", "privacy policy"))

        val testObserver = db.linkConfig().load().test()
        testObserver.awaitValue()
        Assert.assertTrue(testObserver.value().isNotEmpty())
        Assert.assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testClear() {
        val data1 = testLinkConfigData()
        val data2 = testLinkConfigData()
        val data3 = testLinkConfigData()

        val list = mutableListOf(data1, data2, data3)

        db.linkConfig().insertAll(*list.toTypedArray())

        db.linkConfig().clear()

        val testObserver = db.linkConfig().load().test()
        testObserver.awaitValue()
        Assert.assertTrue(testObserver.value().isEmpty())
    }
}
