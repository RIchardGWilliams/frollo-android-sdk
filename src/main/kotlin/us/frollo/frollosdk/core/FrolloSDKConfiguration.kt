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

import us.frollo.frollosdk.authentication.AuthenticationType
import us.frollo.frollosdk.authentication.AuthenticationType.Custom
import us.frollo.frollosdk.authentication.AuthenticationType.OAuth2
import us.frollo.frollosdk.logging.LogLevel
import us.frollo.frollosdk.network.SessionIDProvider

/**
 * Generate SDK configuration.
 *
 * Generates a valid SDK configuration for setting up the SDK. This configuration specifies the authentication method and required parameters to be used.
 * Optional preferences can be set before running FrolloSDK.setup()
 *
 * @param authenticationType Type of authentication to be used. Valid options are [Custom] and [OAuth2]
 * @param clientId OAuth2 Client identifier. The unique identifier of the application implementing the SDK
 * @param serverUrl Base URL of the Frollo API this SDK should point to
 * @param sessionIdProvider Data source to provide client session ID
 * @param databaseNamePrefix Prefix for the name of the database
 * @param logLevel Level of logging for debug and error messages. Default is [LogLevel.ERROR]
 */
data class FrolloSDKConfiguration(
    val authenticationType: AuthenticationType,
    val clientId: String,
    val serverUrl: String,
    val sessionIdProvider: SessionIDProvider? = null,
    val databaseNamePrefix: String? = null,
    val logLevel: LogLevel = LogLevel.ERROR
) {

    internal fun validForROPC(): Boolean {
        if (authenticationType is OAuth2) {
            return clientId.isNotBlank() &&
                authenticationType.tokenUrl.isNotBlank() &&
                serverUrl.isNotBlank()
        }
        return false
    }

    internal fun validForAuthorizationCodeFlow(): Boolean {
        if (authenticationType is OAuth2) {
            return clientId.isNotBlank() &&
                authenticationType.tokenUrl.isNotBlank() &&
                authenticationType.redirectUrl.isNotBlank() &&
                authenticationType.authorizationUrl.isNotBlank() &&
                serverUrl.isNotBlank()
        }
        return false
    }

    internal fun isDAOAuth2LoginEnabled(): Boolean {
        return authenticationType is OAuth2 && authenticationType.daOAuth2Login
    }
}
