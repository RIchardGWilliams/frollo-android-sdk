package us.frollo.frollosdk.authentication

import android.net.Uri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationRequest.Scope
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import us.frollo.frollosdk.core.FrolloSDKConfiguration
import us.frollo.frollosdk.model.oauth.OAuthGrantType
import us.frollo.frollosdk.model.oauth.OAuthTokenRequest

class OAuth(val config: FrolloSDKConfiguration) {

    private val domain: String
        get() = Uri.parse(config.serverUrl).host ?: ""

    internal fun getRefreshTokensRequest(refreshToken: String?) =
            OAuthTokenRequest(
                grantType = OAuthGrantType.REFRESH_TOKEN,
                clientId = config.clientId,
                domain = domain,
                refreshToken = refreshToken)

    internal fun getLoginRequest(username: String, password: String) =
            OAuthTokenRequest(
                    grantType = OAuthGrantType.PASSWORD,
                    clientId = config.clientId,
                    domain = domain,
                    username = username,
                    password = password)

    internal fun getRegisterRequest(username: String, password: String) =
            OAuthTokenRequest(
                    grantType = OAuthGrantType.PASSWORD,
                    clientId = config.clientId,
                    domain = domain,
                    username = username,
                    password = password)

    internal fun getExchangeAuthorizationCodeRequest(code: String, codeVerifier: String? = null) =
            OAuthTokenRequest(
                    grantType = OAuthGrantType.AUTHORIZATION_CODE,
                    clientId = config.clientId,
                    domain = domain,
                    code = code,
                    codeVerifier = codeVerifier,
                    redirectUrl = config.redirectUrl)

    internal fun getExchangeTokenRequest(legacyToken: String) =
            OAuthTokenRequest(
                    grantType = OAuthGrantType.PASSWORD,
                    clientId = config.clientId,
                    domain = domain,
                    legacyToken = legacyToken)

    internal fun getAuthorizationRequest(): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(config.authorizationUri, config.tokenUri)

        val authRequestBuilder = AuthorizationRequest.Builder(
                serviceConfig,
                config.clientId,
                ResponseTypeValues.CODE,
                config.redirectUri)

        return authRequestBuilder
                .setScopes(Scope.OFFLINE_ACCESS, Scope.OPENID, Scope.EMAIL)
                //.setPrompt("login") // Specifies whether the Authorization Server prompts the End-User for re-authentication
                .setAdditionalParameters(mutableMapOf(Pair("audience", config.serverUrl), Pair("domain", domain)))
                .build()
    }
}