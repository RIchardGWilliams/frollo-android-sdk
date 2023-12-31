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

package us.frollo.frollosdk.core

import us.frollo.frollosdk.authentication.AccessTokenProvider
import us.frollo.frollosdk.authentication.AuthenticationCallback
import us.frollo.frollosdk.authentication.AuthenticationType
import us.frollo.frollosdk.logging.LogLevel
import us.frollo.frollosdk.logging.NetworkLoggingProvider
import us.frollo.frollosdk.network.SessionIDProvider
import us.frollo.frollosdk.testutils.randomUUID

internal fun testSDKConfig(
    clientId: String? = null,
    serverUrl: String? = null,
    tokenUrl: String? = null,
    authorizationUrl: String? = null,
    redirectUrl: String? = null,
    revokeTokenURL: String? = null,
    daOAuth2Login: Boolean = false
) =
    FrolloSDKConfiguration(
        authenticationType = AuthenticationType.OAuth2(
            redirectUrl = redirectUrl ?: "app://redirect",
            authorizationUrl = authorizationUrl ?: "https://id.example.com/oauth/authorize/",
            tokenUrl = tokenUrl ?: "https://id.example.com/oauth/token/",
            revokeTokenURL = revokeTokenURL ?: "https://id.example.com/oauth/revoke/",
            daOAuth2Login = daOAuth2Login
        ),
        clientId = clientId ?: "abc123",
        serverUrl = serverUrl ?: "https://api.example.com/",
        sessionIdProvider = MockSessionIDProvider(),
        sdkDBPassphrase = "dbPassphrase123",
        networkLoggingProvider = MockNetworkLoggingProvider()
    )

internal fun testSDKCustomConfig(
    accessTokenProvider: AccessTokenProvider,
    authenticationCallback: AuthenticationCallback,
    clientId: String? = null,
    serverUrl: String? = null
) =
    FrolloSDKConfiguration(
        authenticationType = AuthenticationType.Custom(accessTokenProvider, authenticationCallback),
        clientId = clientId ?: "abc123",
        serverUrl = serverUrl ?: "https://api.example.com/",
        sdkDBPassphrase = "dbPassphrase123",
        networkLoggingProvider = MockNetworkLoggingProvider()
    )

internal class MockSessionIDProvider : SessionIDProvider {
    override val sessionId: String = randomUUID()
}

internal class MockNetworkLoggingProvider : NetworkLoggingProvider {
    override fun logNetworkError(message: String, logLevel: LogLevel) {}
}
