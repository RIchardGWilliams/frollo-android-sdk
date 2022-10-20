package us.frollo.frollosdk.appconfiguration

import com.jraska.livedata.test
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
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.extensions.readStringFromJson
import us.frollo.frollosdk.model.testCompanyConfigData
import us.frollo.frollosdk.model.testFeatureConfigData
import us.frollo.frollosdk.model.testLinkConfigData
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.trimmedPath
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AppConfigurationTest : BaseAndroidTest() {

    override fun initSetup(daOAuth2Login: Boolean) {
        super.initSetup(daOAuth2Login)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
    }

    @Test
    fun testFetchCompanyConfig() {
        initSetup()

        val data = testCompanyConfigData(displayName = "test1")

        database.companyConfig().insert(data)

        val testObserver = appConfiguration.fetchCompanyConfig().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals("test1", testObserver.value()?.displayName)

        tearDown()
    }

    @Test
    fun testFetchFeatureConfig() {
        initSetup()

        val data1 = testFeatureConfigData(key = "key1")
        val data2 = testFeatureConfigData(key = "key2", enabled = true)
        val data3 = testFeatureConfigData()
        val list = mutableListOf(data1, data2, data3)

        database.featureConfig().insertAll(*list.toTypedArray())

        val testObserver = appConfiguration.fetchFeatureConfig().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(3, testObserver.value()?.size)
        assertEquals("key1", testObserver.value()?.get(0)?.key)
        assertEquals(true, testObserver.value()?.get(1)?.enabled)

        tearDown()
    }

    @Test
    fun testFetchLinkConfig() {
        initSetup()

        val data1 = testLinkConfigData(key = "key1")
        val data2 = testLinkConfigData(key = "key2", url = "https://frollo.us/api/pages/explainer_landing_page_android")
        val data3 = testLinkConfigData()
        val list = mutableListOf(data1, data2, data3)

        database.linkConfig().insertAll(*list.toTypedArray())

        val testObserver = appConfiguration.fetchLinkConfig().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(3, testObserver.value()?.size)
        assertEquals("key1", testObserver.value()?.get(0)?.key)
        assertEquals("https://frollo.us/api/pages/explainer_landing_page_android", testObserver.value().data?.get(1)?.url)

        tearDown()
    }

    @Test
    fun testRefreshAppConfig() {
        initSetup()
        val signal = CountDownLatch(1)

        val key = "default-app"

        val requestPath = "config/app/$key"

        val body = readStringFromJson(app, R.raw.app_config)
        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == requestPath) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        // Insert some stale & actual feature config data
        val featureConfigData1 = testFeatureConfigData(key = "key1")
        val featureConfigData2 = testFeatureConfigData(key = "key2")
        val featureConfigData3 = testFeatureConfigData(key = "assets and liabilities", enabled = true)

        val featureConfigList = mutableListOf(featureConfigData1, featureConfigData2, featureConfigData3)
        database.featureConfig().insertAll(*featureConfigList.toTypedArray())

        // Insert some stale & actual Link config data

        val linkConfigData1 = testLinkConfigData(key = "key1")
        val linkConfigData2 = testLinkConfigData(key = "key2")
        val linkConfigData3 = testLinkConfigData(key = "contact us", url = "https://frollo.us/api/pages/explainer_landing_page_android")

        val linkConfigList = mutableListOf(linkConfigData1, linkConfigData2, linkConfigData3)
        database.linkConfig().insertAll(*linkConfigList.toTypedArray())

        // Insert some Company config data
        val companyConfigData = testCompanyConfigData()
        database.companyConfig().insert(companyConfigData)

        appConfiguration.refreshAppConfig(key = key) { result ->

            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            // Company config test
            val testObserver1 = appConfiguration.fetchCompanyConfig().test()
            testObserver1.awaitValue()
            val model1 = testObserver1.value()
            assertNotNull(model1)

            assertEquals("Frollo", model1?.displayName)
            assertEquals("Frollo Australia Pty Ltd", model1?.legalName)
            assertEquals("12345678901", model1?.abn)
            assertEquals("123456789", model1?.acn)
            assertEquals("020000000000", model1?.phone)
            assertEquals("Level 33 100 Mount Street, North Sydney, NSW 2060", model1?.address)
            assertEquals("support@frollo.us", model1?.supportEmail)
            assertEquals("555 02 0000000", model1?.supportPhone)

            // Features config test
            val testObserver2 = appConfiguration.fetchFeatureConfig().test()
            testObserver2.awaitValue()
            val model2 = testObserver2.value()
            assertNotNull(model2)

            assertEquals(3, model2?.size)
            assertEquals("budgets", model2?.get(0)?.key)
            assertEquals("Budgeting", model2?.get(0)?.name)
            assertEquals(true, model2?.get(2)?.enabled)

            // Verify that the stale feature configs are deleted from the database
            assertEquals(0, model2?.filter { it.key in listOf("key1", "key2") }?.size)

            // Links config test
            val testObserver3 = appConfiguration.fetchLinkConfig().test()
            testObserver3.awaitValue()
            val model3 = testObserver3.value()
            assertNotNull(model3)

            assertEquals(3, model3?.size)
            assertEquals("terms", model3?.get(0)?.key)
            assertEquals("Terms and Conditions", model3?.get(0)?.name)
            assertEquals("https://frollo.us/terms", model3?.get(0)?.url)
            assertEquals("https://frollo.us/api/pages/explainer_landing_page_android", model3?.get(2)?.url)

            // Verify that the stale link configs are deleted from the database
            assertEquals(0, model3?.filter { it.key in listOf("key1", "key2") }?.size)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testRefreshAppConfigNoAuthHeaders() {
        initSetup()

        val key = "FROLLO_FINANCE"
        val url = "config/app/$key"

        val body = readStringFromJson(app, R.raw.app_config)
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

        assertNull(request.getHeader("Authorization"))
        assertNull(request.getHeader("X-Api-Version"))
        assertNull(request.getHeader("X-Bundle-Id"))
        assertNull(request.getHeader("X-Device-Version"))
        assertNull(request.getHeader("X-Software-Version"))
        assertNull(request.getHeader("X-Session-Id"))

        tearDown()
    }
}
