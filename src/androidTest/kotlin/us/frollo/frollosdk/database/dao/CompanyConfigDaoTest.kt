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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.model.testCompanyConfigData

class CompanyConfigDaoTest {

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
    fun testLoad() {
        val testObserver = db.companyConfig().load().test()
        testObserver.awaitValue()
        assertNull(testObserver.value())

        db.companyConfig().insert(testCompanyConfigData())

        val testObserver2 = db.companyConfig().load().test()
        testObserver2.awaitValue()
        assertNotNull(testObserver2.value())
    }

    @Test
    fun testLoadByQuery() {
        val data = testCompanyConfigData()

        db.companyConfig().insert(data)

        val query = SimpleSQLiteQuery("SELECT * FROM company_config")

        val testObserver = db.companyConfig().loadByQuery(query).test()
        testObserver.awaitValue()
        assertEquals(data.displayName, testObserver.value()?.displayName)
    }

    @Test
    fun testInsert() {
        val data = testCompanyConfigData()
        db.companyConfig().insert(data)

        val testObserver = db.companyConfig().load().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(data.displayName, testObserver.value()?.displayName)
    }

    @Test
    fun testClear() {
        val data = testCompanyConfigData()
        db.companyConfig().insert(data)

        db.companyConfig().clear()
        val testObserver = db.companyConfig().load().test()
        testObserver.awaitValue()
        assertNull(testObserver.value())
    }
}
