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

package us.frollo.frollosdk.error

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import net.openid.appauth.AuthorizationException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson

class OAuth2ErrorTest {

    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    @Before
    fun setUp() {
        FrolloSDK.context = app
    }

    @Test
    fun testAccessDeniedError() {
        val exception = AuthorizationException.AuthorizationRequestErrors.ACCESS_DENIED
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.ACCESS_DENIED, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.ACCESS_DENIED.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testClientError() {
        val exception = AuthorizationException.AuthorizationRequestErrors.CLIENT_ERROR
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.CLIENT_ERROR, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.CLIENT_ERROR.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testInvalidClientError() {
        val exception = AuthorizationException.TokenRequestErrors.INVALID_CLIENT
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.INVALID_CLIENT, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.INVALID_CLIENT.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testInvalidClientMetadataError() {
        val exception = AuthorizationException.RegistrationRequestErrors.INVALID_CLIENT_METADATA
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.INVALID_CLIENT_METADATA, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.INVALID_CLIENT_METADATA.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testInvalidGrantError() {
        val exception = AuthorizationException.TokenRequestErrors.INVALID_GRANT
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.INVALID_GRANT, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.INVALID_GRANT.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testInvalidRedirectURIError() {
        val exception = AuthorizationException.RegistrationRequestErrors.INVALID_REDIRECT_URI
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.INVALID_REDIRECT_URI, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.INVALID_REDIRECT_URI.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testInvalidRequestError() {
        val exception = AuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.INVALID_REQUEST, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.INVALID_REQUEST.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testInvalidScopeError() {
        val exception = AuthorizationException.AuthorizationRequestErrors.INVALID_SCOPE
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.INVALID_SCOPE, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.INVALID_SCOPE.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testUnauthorizedClientError() {
        val exception = AuthorizationException.AuthorizationRequestErrors.UNAUTHORIZED_CLIENT
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.UNAUTHORIZED_CLIENT, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.UNAUTHORIZED_CLIENT.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testUnsupportedGrantTypeError() {
        val exception = AuthorizationException.TokenRequestErrors.UNSUPPORTED_GRANT_TYPE
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.UNSUPPORTED_GRANT_TYPE, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.UNSUPPORTED_GRANT_TYPE.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testUnsupportedResponseTypeError() {
        val exception = AuthorizationException.AuthorizationRequestErrors.UNSUPPORTED_RESPONSE_TYPE
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.UNSUPPORTED_RESPONSE_TYPE, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.UNSUPPORTED_RESPONSE_TYPE.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testNetworkError() {
        val exception = AuthorizationException.GeneralErrors.NETWORK_ERROR
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.NETWORK_ERROR, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.NETWORK_ERROR.textResource)
        assertEquals(true, authError.localizedDescription?.contains(localizedDescription))
    }

    @Test
    fun testServerError() {
        val exception = AuthorizationException.AuthorizationRequestErrors.SERVER_ERROR
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.SERVER_ERROR, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.SERVER_ERROR.textResource)
        assertEquals(localizedDescription, authError.localizedDescription)
    }

    @Test
    fun testUserCancelledError() {
        val exception = AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.USER_CANCELLED, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.USER_CANCELLED.textResource)
        assertEquals(true, authError.localizedDescription?.contains(localizedDescription))
    }

    @Test
    fun testOtherAuthorisationError() {
        val exception = AuthorizationException.GeneralErrors.INVALID_DISCOVERY_DOCUMENT
        val authError = OAuth2Error(exception = exception)
        assertEquals(OAuth2ErrorType.OTHER_AUTHORIZATION, authError.type)
        val localizedDescription = app.resources.getString(OAuth2ErrorType.OTHER_AUTHORIZATION.textResource)
        assertEquals(true, authError.localizedDescription?.contains(localizedDescription))
    }

    @Test
    fun testOAuth2InvalidClientError() {
        val errorResponse = readStringFromJson(app, R.raw.error_oauth2_invalid_client)

        val authError = OAuth2Error(response = errorResponse)
        assertEquals(OAuth2ErrorType.INVALID_CLIENT, authError.type)
        assertEquals(
            "An authorization error occurring on the client rather than the server. For example, due to a state mismatch or misconfiguration. Should be treated as an unrecoverable authorization error.\n" +
                "\n" +
                "Invalid client request",
            authError.localizedDescription
        )
    }

    @Test
    fun testOAuth2InvalidGrantError() {
        val errorResponse = readStringFromJson(app, R.raw.error_oauth2_invalid_grant)

        val authError = OAuth2Error(response = errorResponse)
        assertEquals(OAuth2ErrorType.INVALID_GRANT, authError.type)
        assertEquals(
            "The provided authorization grant (e.g., authorization code, resource owner credentials) or refresh token is invalid, expired, revoked, does not match the redirection URI used in the authorization request, or was issued to another client.\n" +
                "\n" +
                "Invalid Grant Request",
            authError.localizedDescription
        )
    }

    @Test
    fun testOAuth2InvalidRequestError() {
        val errorResponse = readStringFromJson(app, R.raw.error_oauth2_invalid_request)

        val authError = OAuth2Error(response = errorResponse)
        assertEquals(OAuth2ErrorType.INVALID_REQUEST, authError.type)
        assertEquals(
            "The request is missing a required parameter, includes an unsupported parameter value (other than grant type), repeats a parameter, includes multiple credentials, utilizes more than one mechanism for authenticating the client, or is otherwise malformed.\n" +
                "\n" +
                "Request was missing the 'redirect_uri' parameter.",
            authError.localizedDescription
        )
        assertEquals("See the full API docs at https://authorization-server.com/docs/access_token", authError.errorUri)
    }

    @Test
    fun testOAuth2InvalidScopeError() {
        val errorResponse = readStringFromJson(app, R.raw.error_oauth2_invalid_scope)

        val authError = OAuth2Error(response = errorResponse)
        assertEquals(OAuth2ErrorType.INVALID_SCOPE, authError.type)
        assertEquals(
            "The requested scope is invalid, unknown, or malformed.\n" +
                "\n" +
                "Invalid scope request.",
            authError.localizedDescription
        )
    }

    @Test
    fun testOAuth2ServerError() {
        val errorResponse = readStringFromJson(app, R.raw.error_oauth2_server)

        val authError = OAuth2Error(response = errorResponse)
        assertEquals(OAuth2ErrorType.SERVER_ERROR, authError.type)
        assertEquals(
            "Indicates a server error occurred.\n" +
                "\n" +
                "Authorization server not configured with default connection.",
            authError.localizedDescription
        )
    }

    @Test
    fun testOAuth2UnauthorizedClientError() {
        val errorResponse = readStringFromJson(app, R.raw.error_oauth2_unauthorized_client)

        val authError = OAuth2Error(response = errorResponse)
        assertEquals(OAuth2ErrorType.UNAUTHORIZED_CLIENT, authError.type)
        assertEquals(
            "The client is not authorized to request an authorization code using this method.\n" +
                "\n" +
                "Unauthorized client request.",
            authError.localizedDescription
        )
    }
}
