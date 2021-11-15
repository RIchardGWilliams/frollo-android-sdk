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

package us.frollo.frollosdk.authentication

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import us.frollo.frollosdk.authentication.AuthenticationType.OAuth2
import us.frollo.frollosdk.core.FrolloSDKConfiguration
import us.frollo.frollosdk.model.oauth.OAuthGrantType
import us.frollo.frollosdk.model.oauth.OAuthTokenRequest
import us.frollo.frollosdk.model.oauth.OAuthTokenRevokeRequest

/**
 * @suppress
 */
class OAuth2Helper(val config: FrolloSDKConfiguration) {

    internal val oAuth2: OAuth2
        get() = config.authenticationType as OAuth2

    private val domain: String
        get() = Uri.parse(config.serverUrl).host ?: ""

    private val audience: String
        get() {
            val audUrl = oAuth2.audienceUrl
            return if (audUrl?.isNotBlank() == true) audUrl else config.serverUrl
        }

    internal fun getRefreshTokensRequest(refreshToken: String?) =
        OAuthTokenRequest(
            grantType = OAuthGrantType.REFRESH_TOKEN,
            clientId = config.clientId,
            domain = domain,
            refreshToken = refreshToken
        )

    internal fun getLoginRequest(username: String, password: String, scopes: List<String>, grantType: OAuthGrantType) =
        OAuthTokenRequest(
            grantType = grantType,
            clientId = config.clientId,
            domain = domain,
            username = username,
            password = password,
            audience = audience,
            scope = scopes.joinToString(" "),
            realm = "Username-Password-Authentication" // Note: Needed for Volt
        )

    internal fun getRegisterRequest(username: String, password: String, scopes: List<String>, grantType: OAuthGrantType) =
        OAuthTokenRequest(
            grantType = grantType,
            clientId = config.clientId,
            domain = domain,
            username = username,
            password = password,
            audience = config.serverUrl,
            scope = scopes.joinToString(" ")
        )

    internal fun getExchangeAuthorizationCodeRequest(scopes: List<String>, code: String, codeVerifier: String? = null) =
        OAuthTokenRequest(
            grantType = OAuthGrantType.AUTHORIZATION_CODE,
            clientId = config.clientId,
            domain = domain,
            code = code,
            codeVerifier = codeVerifier,
            redirectUrl = oAuth2.redirectUrl,
            audience = config.serverUrl,
            scope = scopes.joinToString(" ")
        )

    internal fun getExchangeAuthorizationCodeRequestForDAOAuth2Login(code: String) =
        OAuthTokenRequest(
            grantType = OAuthGrantType.AUTHORIZATION_CODE,
            clientId = config.clientId,
            domain = domain,
            code = code,
            redirectUrl = oAuth2.redirectUrl
        )

    internal fun getExchangeTokenRequest(legacyToken: String, scopes: List<String>) =
        OAuthTokenRequest(
            grantType = OAuthGrantType.PASSWORD,
            clientId = config.clientId,
            domain = domain,
            legacyToken = legacyToken,
            audience = config.serverUrl,
            scope = scopes.joinToString(" ")
        )

    internal fun getAuthorizationRequest(scopes: List<String>, additionalParameters: Map<String, String>? = null, clientId: String? = null): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            oAuth2.authorizationUri,
            oAuth2.tokenUri
        )

        val authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig,
            clientId ?: config.clientId, // Use clientId from FrolloSDKConfiguration if the custom clientId is not provided
            ResponseTypeValues.CODE,
            oAuth2.redirectUri
        )

        val customParameters = mutableMapOf(Pair("audience", audience), Pair("domain", domain))
        additionalParameters?.let { customParameters.putAll(it) }

        return authRequestBuilder
            .setScopes(scopes)
            .setPrompt(AuthorizationRequest.Prompt.LOGIN) // Specifies whether the Authorization Server prompts the End-User for re-authentication. Without this the chrome tabs will remember the last user logged in and there is no way we can clear the cookies.
            .setAdditionalParameters(customParameters)
            .build()
    }

    internal fun getCustomTabsIntent(service: AuthorizationService, request: AuthorizationRequest, toolBarColor: Int? = null): CustomTabsIntent {
        val intentBuilder = service.createCustomTabsIntentBuilder(request.toUri())
        toolBarColor?.let { intentBuilder.setToolbarColor(it) }
        return intentBuilder.build()
    }

    internal fun getTokenRevokeRequest(refreshToken: String) =
        OAuthTokenRevokeRequest(
            clientId = config.clientId,
            token = refreshToken,
            domain = domain
        )
}
