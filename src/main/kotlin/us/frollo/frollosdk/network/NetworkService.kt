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

package us.frollo.frollosdk.network

import android.os.Build
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.frollo.frollosdk.BuildConfig
import us.frollo.frollosdk.authentication.AccessTokenProvider
import us.frollo.frollosdk.authentication.AuthToken
import us.frollo.frollosdk.authentication.AuthenticationCallback
import us.frollo.frollosdk.authentication.AuthenticationType.OAuth2
import us.frollo.frollosdk.authentication.OAuth2Helper
import us.frollo.frollosdk.base.LiveDataCallAdapterFactory
import us.frollo.frollosdk.core.AppInfo
import us.frollo.frollosdk.keystore.Keystore
import us.frollo.frollosdk.model.api.contacts.ContactCreateUpdateRequest
import us.frollo.frollosdk.model.api.contacts.ContactResponse
import us.frollo.frollosdk.network.deserializer.ContactResponseDeserializer
import us.frollo.frollosdk.network.serializer.ContactRequestSerializer
import us.frollo.frollosdk.preferences.Preferences
import java.util.concurrent.TimeUnit

class NetworkService internal constructor(
    internal val oAuth2Helper: OAuth2Helper,
    keystore: Keystore,
    pref: Preferences,
    appInfo: AppInfo
) : IApiProvider {

    companion object {
        private const val TAG = "NetworkService"
        private const val PINNING_PATTERN = "*.frollo.us"
    }

    internal val authToken = AuthToken(keystore, pref)
    private val helper = NetworkHelper(appInfo)
    private val serverInterceptor = NetworkInterceptor(this, helper)
    private val tokenInterceptor = TokenInterceptor(helper, oAuth2Helper.config)

    private val apiRetrofit = createRetrofit(baseUrl = oAuth2Helper.config.serverUrl, isTokenEndpoint = false)
    private val authRetrofit: Retrofit?
        get() {
            return if (oAuth2Helper.config.authenticationType is OAuth2)
                createRetrofit(baseUrl = oAuth2Helper.oAuth2.tokenUrl, isTokenEndpoint = true)
            else null
        }
    private var revokeTokenRetrofit: Retrofit? = null
    private val externalNoAuthRetrofit = createRetrofit(
        // Need to pass this as base URL cannot be empty for Retrofit initialization.
        // But, this will be ignored anyways when we specify full URL in the Service API.
        baseUrl = oAuth2Helper.config.serverUrl,
        isTokenEndpoint = false,
        needAuthentication = false
    )

    internal var accessTokenProvider: AccessTokenProvider? = null
    internal var authenticationCallback: AuthenticationCallback? = null

    internal var invalidTokenRetries: Int = 0
    private var dispatcher: Dispatcher? = null

    init {
        if (oAuth2Helper.config.authenticationType is OAuth2) {
            oAuth2Helper.oAuth2.revokeTokenURL?.let { revokeTokenUrl ->
                revokeTokenRetrofit = createRetrofit(baseUrl = revokeTokenUrl, isTokenEndpoint = true)
            }
        }
    }

    private fun createRetrofit(baseUrl: String, isTokenEndpoint: Boolean, needAuthentication: Boolean = true): Retrofit {
        val httpClientBuilder = OkHttpClient.Builder()
            .setTimeouts()
            .addCertificatePinning()

        if (needAuthentication) {
            httpClientBuilder.addInterceptors(isTokenEndpoint)
                .addAuthenticators(isTokenEndpoint)
        }

        val httpClient = httpClientBuilder.build()

        // Keep a reference to the dispatcher for host service so we can remove requests on reset
        if (!isTokenEndpoint && needAuthentication) {
            dispatcher = httpClient.dispatcher
        }

        val gson = GsonBuilder()
            .registerTypeAdapter(ContactResponse::class.java, ContactResponseDeserializer)
            .registerTypeAdapter(ContactCreateUpdateRequest::class.java, ContactRequestSerializer)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .enableComplexMapKeySerialization()
            .create()

        val builder = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory)

        return builder.build()
    }

    override fun <T> create(service: Class<T>): T = apiRetrofit.create(service)
    override fun <T> createAuth(service: Class<T>): T? = authRetrofit?.create(service)
    override fun <T> createRevoke(service: Class<T>): T? = revokeTokenRetrofit?.create(service)
    override fun <T> createExternalNoAuth(service: Class<T>): T = externalNoAuthRetrofit.create(service)

    private fun OkHttpClient.Builder.addInterceptors(isTokenEndpoint: Boolean): OkHttpClient.Builder {
        addInterceptor(
            if (isTokenEndpoint)
                tokenInterceptor
            else
                serverInterceptor
        )
        return this
    }

    private fun OkHttpClient.Builder.setTimeouts(): OkHttpClient.Builder {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        writeTimeout(30, TimeUnit.SECONDS)
        return this
    }

    private fun OkHttpClient.Builder.addAuthenticators(isTokenEndpoint: Boolean): OkHttpClient.Builder {
        authenticator(
            if (isTokenEndpoint)
                TokenAuthenticator(this@NetworkService)
            else
                NetworkAuthenticator(this@NetworkService)
        )
        return this
    }

    private fun OkHttpClient.Builder.addCertificatePinning(): OkHttpClient.Builder {
        if (!BuildConfig.DEBUG && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val certPinner = CertificatePinner.Builder()
                .add(PINNING_PATTERN, PublicKey.ACTIVE)
                .add(PINNING_PATTERN, PublicKey.BACKUP)
                .build()
            certificatePinner(certPinner)
        }
        return this
    }

    internal fun authenticateRequest(request: Request): Request {
        return serverInterceptor.authenticateRequest(request)
    }

    internal fun reset() {
        invalidTokenRetries = 0
        dispatcher?.queuedCalls()?.forEach { it.cancel() }
    }

    internal fun tokenInvalidated() {
        reset()

        authenticationCallback?.tokenInvalidated()
    }
}
