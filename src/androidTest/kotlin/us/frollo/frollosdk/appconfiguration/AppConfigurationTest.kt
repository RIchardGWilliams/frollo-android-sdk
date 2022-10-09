package us.frollo.frollosdk.appconfiguration

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.BaseAndroidTest
import us.frollo.frollosdk.extensions.readStringFromJson
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
    fun testFetchAppConfig() {
        initSetup()
        val signal = CountDownLatch(1)

        val key = "FROLLO_FINANCE"

        val requestPath = "api/v2/config/app/$key"

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

        appConfiguration.fetchAppConfig(key = key) { resource ->

            val response = resource.data
            assertNotNull(response)

            assertEquals("Frollo", response?.company?.displayName)
            assertEquals("Frollo Australia Pty Ltd", response?.company?.legalName)
            assertEquals("12345678901", response?.company?.abn)
            assertEquals("123456789", response?.company?.acn)
            assertEquals("020000000000", response?.company?.phone)
            assertEquals("Level 33 100 Mount Street, North Sydney, NSW 2060", response?.company?.address)
            assertEquals("support@frollo.us", response?.company?.supportEmail)
            assertEquals("555 02 0000000", response?.company?.supportPhone)

            assertEquals("terms", response?.links?.get(0)?.key)
            assertEquals("Terms and Conditions", response?.links?.get(0)?.name)
            assertEquals("https://frollo.us/terms", response?.links?.get(0)?.url)

            assertEquals("budgets", response?.features?.get(0)?.key)
            assertEquals("Budgeting", response?.features?.get(0)?.name)
            assertEquals(true, response?.features?.get(0)?.enabled)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(requestPath, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }
}
