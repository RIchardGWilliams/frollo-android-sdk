package us.frollo.frollosdk.appconfiguration

import com.jraska.livedata.test
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.BaseAndroidTest
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.extensions.readStringFromJson
import us.frollo.frollosdk.network.api.AppConfigurationAPI
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.trimmedPath
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AppConfigurationTest : BaseAndroidTest() {

    private lateinit var appConfigurationAPI: AppConfigurationAPI

    override fun initSetup(daOAuth2Login: Boolean) {
        super.initSetup(daOAuth2Login)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        appConfigurationAPI = network.create(AppConfigurationAPI::class.java)
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

        appConfiguration.refreshAppConfig(key = key) { result ->

            assertEquals(Result.Status.SUCCESS, result.status)
            assertNull(result.error)

            // Company config test
            val testObserver1 = appConfiguration.fetchCompanyConfig().test()
            testObserver1.awaitValue()
            val model1 = testObserver1.value()
            assertNotNull(model1)

            assertEquals("Frollo", model1?.data?.displayName)
            assertEquals("Frollo Australia Pty Ltd", model1?.data?.legalName)
            assertEquals("12345678901", model1?.data?.abn)
            assertEquals("123456789", model1?.data?.acn)
            assertEquals("020000000000", model1?.data?.phone)
            assertEquals("Level 33 100 Mount Street, North Sydney, NSW 2060", model1?.data?.address)
            assertEquals("support@frollo.us", model1?.data?.supportEmail)
            assertEquals("555 02 0000000", model1?.data?.supportPhone)

            // Features config test
            val testObserver2 = appConfiguration.fetchFeatureConfig().test()
            testObserver2.awaitValue()
            val model2 = testObserver2.value()
            assertNotNull(model2)

            assertEquals("budgets", model2?.data?.get(0)?.key)
            assertEquals("Budgeting", model2?.data?.get(0)?.name)
            assertEquals(true, model2?.data?.get(0)?.enabled)

            // Links config test
            val testObserver3 = appConfiguration.fetchLinkConfig().test()
            testObserver3.awaitValue()
            val model3 = testObserver3.value()
            assertNotNull(model3)

            assertEquals("terms", model3?.data?.get(0)?.key)
            assertEquals("Terms and Conditions", model3?.data?.get(0)?.name)
            assertEquals("https://frollo.us/terms", model3?.data?.get(0)?.url)

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
