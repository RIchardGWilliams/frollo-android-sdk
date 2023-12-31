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

package us.frollo.frollosdk.extensions

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.LiveDataCallAdapterFactory
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.error.APIError
import us.frollo.frollosdk.error.APIErrorType
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.DataErrorSubType
import us.frollo.frollosdk.error.DataErrorType
import us.frollo.frollosdk.error.FrolloSDKError
import us.frollo.frollosdk.error.NetworkError
import us.frollo.frollosdk.error.NetworkErrorType
import us.frollo.frollosdk.error.OAuth2Error
import us.frollo.frollosdk.error.OAuth2ErrorType
import us.frollo.frollosdk.network.ApiResponse
import us.frollo.frollosdk.network.ErrorResponseType
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.TestAPI
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.trimmedPath
import java.io.IOException
import java.net.SocketTimeoutException
import java.security.cert.CertPathValidatorException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

class NetworkExtensionTest {

    private val app =
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    private lateinit var mockServer: MockWebServer
    private lateinit var testAPI: TestAPI

    private fun createRetrofit(baseUrl: String): Retrofit {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .enableComplexMapKeySerialization()
            .create()

        val httpClient = OkHttpClient.Builder()
            .build()

        val builder = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(LiveDataCallAdapterFactory)

        return builder.build()
    }

    private fun initSetup() {
        FrolloSDK.context = app

        mockServer = MockWebServer()
        mockServer.start()
        val baseUrl = mockServer.url("/")
        val retrofit = createRetrofit(baseUrl.toString())
        testAPI = retrofit.create(TestAPI::class.java)
    }

    private fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun testNetworkCallResponseSuccess() {
        initSetup()

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == TestAPI.URL_TEST) {
                        return MockResponse()
                            .setResponseCode(200)
                            .setBody("{}")
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        testAPI.testData().enqueue { resource ->
            assertEquals(Resource.Status.SUCCESS, resource.status)
            assertNull(resource.error)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(TestAPI.URL_TEST, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testNetworkCallResponseFailure() {
        initSetup()

        val signal = CountDownLatch(1)

        mockServer.dispatcher = (
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.trimmedPath == TestAPI.URL_TEST) {
                        return MockResponse()
                            .setResponseCode(401)
                            .setBody("{}")
                    }
                    return MockResponse().setResponseCode(404)
                }
            }
            )

        testAPI.testData().enqueue { resource ->
            assertEquals(Resource.Status.ERROR, resource.status)
            assertNotNull(resource.error)
            assertTrue(resource.error is APIError)
            assertEquals(APIErrorType.OTHER_AUTHORISATION, (resource.error as APIError).type)

            signal.countDown()
        }

        val request = mockServer.takeRequest()
        assertEquals(TestAPI.URL_TEST, request.trimmedPath)

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testNetworkCallFailure() {
        initSetup()

        val signal = CountDownLatch(1)

        mockServer.enqueue(
            MockResponse().apply {
                socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
            }
        )

        testAPI.testData().enqueue { resource ->
            assertEquals(Resource.Status.ERROR, resource.status)
            assertNotNull(resource.error)
            assertTrue(resource.error is NetworkError)
            assertEquals(NetworkErrorType.CONNECTION_FAILURE, (resource.error as NetworkError).type)

            signal.countDown()
        }

        signal.await(3, TimeUnit.SECONDS)

        tearDown()
    }

    @Test
    fun testHandleFailureDataError() {
        initSetup()

        val errorMessage =
            DataError(DataErrorType.API, DataErrorSubType.MISSING_ACCESS_TOKEN).toJson()

        handleFailure<Void>(
            ErrorResponseType.NORMAL,
            ApiResponse(IOException(errorMessage))
        ) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is DataError)
            assertEquals(DataErrorType.API, (result.error as DataError).type)
            assertEquals(DataErrorSubType.MISSING_ACCESS_TOKEN, (result.error as DataError).subType)
        }

        tearDown()
    }

    @Test
    fun testHandleFailureAPIError() {
        initSetup()

        val errorBody = ResponseBody.create(
            "application/json".toMediaTypeOrNull(),
            readStringFromJson(app, R.raw.error_invalid_access_token)
        )

        handleFailure<Void>(
            ErrorResponseType.NORMAL,
            ApiResponse(Response.error(401, errorBody))
        ) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is APIError)
            assertEquals(APIErrorType.INVALID_ACCESS_TOKEN, (result.error as APIError).type)
        }

        tearDown()
    }

    @Test
    fun testHandleFailureGenericError() {
        initSetup()

        val apiResponse = ApiResponse<Void>(
            Response.error(
                401,
                ResponseBody.create("text".toMediaTypeOrNull(), "error")
            )
        )
        apiResponse.code = null

        handleFailure(ErrorResponseType.NORMAL, apiResponse) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is FrolloSDKError)
            assertEquals("error", result.error?.localizedDescription)
        }

        tearDown()
    }

    @Test
    fun testHandleFailureNetworkErrorIOException() {
        initSetup()

        handleFailure<Void>(
            ErrorResponseType.NORMAL,
            ApiResponse(SocketTimeoutException("timeout")),
            SocketTimeoutException("timeout")
        ) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is NetworkError)
            assertEquals(NetworkErrorType.CONNECTION_FAILURE, (result.error as NetworkError).type)
            assertEquals("timeout", (result.error as NetworkError).message)
        }

        tearDown()
    }

    @Test
    fun testHandleFailureNetworkErrorSSLException() {
        initSetup()

        handleFailure<Void>(
            ErrorResponseType.NORMAL,
            ApiResponse(SSLException("SSL Error")),
            SSLException("SSL Error")
        ) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is NetworkError)
            assertEquals(NetworkErrorType.INVALID_SSL, (result.error as NetworkError).type)
            assertEquals("SSL Error", (result.error as NetworkError).message)
        }

        tearDown()
    }

    @Test
    fun testHandleFailureNetworkErrorGeneralSecurityException() {
        initSetup()

        handleFailure<Void>(
            ErrorResponseType.NORMAL,
            ApiResponse(CertPathValidatorException("Certificate Error")),
            CertPathValidatorException("Certificate Error")
        ) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is NetworkError)
            assertEquals(NetworkErrorType.INVALID_SSL, (result.error as NetworkError).type)
            assertEquals("Certificate Error", (result.error as NetworkError).message)
        }

        tearDown()
    }

    @Test
    fun testHandleFailureNetworkErrorOtherException() {
        initSetup()

        handleFailure<Void>(
            ErrorResponseType.NORMAL,
            ApiResponse(IllegalStateException("Conversion Error")),
            IllegalStateException("Conversion Error")
        ) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is NetworkError)
            assertEquals(NetworkErrorType.UNKNOWN, (result.error as NetworkError).type)
            assertEquals("Conversion Error", (result.error as NetworkError).message)
        }

        tearDown()
    }

    @Test
    fun testHandleFailureOAuth2Error() {
        initSetup()

        val errorBody = ResponseBody.create(
            "application/json".toMediaTypeOrNull(),
            readStringFromJson(app, R.raw.error_oauth2_invalid_client)
        )

        handleFailure<Void>(
            ErrorResponseType.OAUTH2,
            ApiResponse(Response.error(401, errorBody))
        ) { result ->
            assertEquals(Resource.Status.ERROR, result.status)
            assertNotNull(result.error)
            assertTrue(result.error is OAuth2Error)
            assertEquals(OAuth2ErrorType.INVALID_CLIENT, (result.error as OAuth2Error).type)
            assertEquals(401, (result.error as OAuth2Error).statusCode)
        }

        tearDown()
    }
}
