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

package us.frollo.frollosdk.logging

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.BaseAndroidTest

class NetworkLoggerTest : BaseAndroidTest() {

    private var networkLogMessage = ""

    override fun initSetup(daOAuth2Login: Boolean) {
        super.initSetup(daOAuth2Login)

        preferences.loggedIn = true
        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900
    }

    @Test
    fun testLogging() {
        initSetup()

        val logger = NetworkLogger(
            networkLoggingProvider = object : NetworkLoggingProvider {
                override fun logNetworkError(message: String, logLevel: LogLevel) {
                    networkLogMessage = message
                }
            }
        )
        logger.writeMessage("Test Message", LogLevel.ERROR)

        assertEquals("Test Message", networkLogMessage)

        tearDown()
    }

    // Keeping our legacy implementation just-in-case we need to revert back in future
    /*@Test
    fun testLogging() {
        initSetup()

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == DeviceAPI.URL_LOG) {
                        return MockResponse()
                            .setResponseCode(201)
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        val logger = NetworkLogger(network, deviceId = randomUUID(), deviceName = randomString(12), deviceType = randomString(12))
        logger.writeMessage("Test Message", LogLevel.ERROR)

        val request = mockServer.takeRequest()
        assertEquals(DeviceAPI.URL_LOG, request.trimmedPath)

        tearDown()
    }*/
}
