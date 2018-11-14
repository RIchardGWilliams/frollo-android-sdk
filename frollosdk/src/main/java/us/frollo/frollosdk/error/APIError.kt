package us.frollo.frollosdk.error

import us.frollo.frollosdk.extensions.toAPIErrorResponse
import us.frollo.frollosdk.extensions.toAPIErrorType
import us.frollo.frollosdk.model.api.shared.APIErrorCode
import us.frollo.frollosdk.model.api.shared.APIErrorResponse

class APIError(val statusCode: Int, errorMessage: String) : FrolloSDKError(errorMessage) {

    // Type of API Error
    val type : APIErrorType
        get() = statusCode.toAPIErrorType(errorCode)

    // Error code returned by the API if available and recognised
    val errorCode: APIErrorCode?
        get() = errorResponse?.errorCode

    // Error message returned by the API if available
    val message : String?
        get() = errorResponse?.errorMessage

    private var errorResponse: APIErrorResponse? = null

    init {
        errorResponse = errorMessage.toAPIErrorResponse()
    }

    override val localizedDescription: String
        get() = type.toLocalizedString(context)

    override val debugDescription: String
        get() {
            val debug = "APIError: Type [${ type.name }] HTTP Status Code: $statusCode "
            errorCode?.let { debug.plus("$it: ") }
            message?.let { debug.plus("$it | ") }
            debug.plus(localizedDescription)
            return debug
        }
}