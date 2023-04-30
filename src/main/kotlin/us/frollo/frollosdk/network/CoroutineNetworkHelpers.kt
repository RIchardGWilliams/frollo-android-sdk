package us.frollo.frollosdk.network

import retrofit2.Response
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.core.ACTION
import us.frollo.frollosdk.error.APIError
import us.frollo.frollosdk.error.DataError
import us.frollo.frollosdk.error.FrolloSDKError
import us.frollo.frollosdk.error.NetworkError
import us.frollo.frollosdk.error.OAuth2Error
import us.frollo.frollosdk.extensions.handleOAuth2Failure
import us.frollo.frollosdk.extensions.notify
import us.frollo.frollosdk.mapping.toDataError
import us.frollo.frollosdk.mapping.toOAuth2ErrorResponse

/**
 * A simple wrapper to handle all network calls for coroutine operations.
 * There are handling of errors in line with existing logic. The goal is to wrap all network
 * calls and handle any errors
 *
 * @param operation The call being made, GET/POST/PUT/DELETE etc
 * @return The resource being returned from operation, errors are handled the same way as
 * @see handleFailure
 * @see NetworkError
 */
internal suspend fun <T : Any> makeApiCall(operation: suspend () -> Response<T>): Resource<T> {
    var apiResponse: ApiResponse<T>? = null
    return try {
        apiResponse = ApiResponse(operation())
        return if (apiResponse.isSuccessful) {
            Resource.success(data = apiResponse.body, responseStatusCode = apiResponse.code)
        } else {
            val errorResponseType = ErrorResponseType.NORMAL
            processNetworkResponseError(errorResponseType, apiResponse, null)
        }
    } catch (e: Exception) {
        processNetworkResponseError(ErrorResponseType.NORMAL, apiResponse!!, e)
    }
}

internal fun <T> processNetworkResponseError(
    errorResponseType: ErrorResponseType,
    errorResponse: ApiResponse<T>,
    t: Throwable? = null
): Resource<T> {
    val code = errorResponse.code
    val errorMsg = errorResponse.errorMessage

    val oAuth2ErrorResponse = errorMsg?.toOAuth2ErrorResponse()
    val dataError = errorMsg?.toDataError()

    if (code == 410) {
        notify(action = ACTION.ACTION_410_ERROR)
    }

    if (dataError != null) {
        return Resource.error(error = DataError(dataError.type, dataError.subType)) // Re-create new DataError as the json converter does not has the context object
    } else if (errorResponseType == ErrorResponseType.OAUTH2 && oAuth2ErrorResponse != null) {
        val oAuth2Error = OAuth2Error(statusCode = code, response = errorMsg)
        handleOAuth2Failure(oAuth2Error)
        return Resource.error(error = oAuth2Error)
    } else if (errorResponseType == ErrorResponseType.NORMAL && code != null) {
        return Resource.error(error = APIError(code, errorMsg))
    } else if (t != null) {
        return Resource.error(error = NetworkError(t))
    } else {
        return Resource.error(error = FrolloSDKError(errorMsg))
    }
}
