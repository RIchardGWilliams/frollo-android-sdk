package us.frollo.frollosdk.base

import us.frollo.frollosdk.error.FrolloSDKError

/**
 * Wrapper class around the data with different fetch states
 */
class Resource<out T> private constructor(
        /**
         * Status of the fetch result
         */
        val status: Status,
        /**
         * Fetched data. null if state is [Status.ERROR]
         */
        val data: T? = null,
        /**
         * Error details if state is [Status.ERROR]
         */
        val error: FrolloSDKError? = null) {

    /**
     * Enum of fetch result states
     */
    enum class Status {
        /**
         * Indicates data fetched successfully.
         */
        SUCCESS,
        /**
         * Indicates error while fetching data.
         */
        ERROR
    }

    /**
     * Maps the [Resource] data into a new [Resource] object with new data, while copying the other properties
     */
    fun <Y> map(function: (T?) -> Y?): Resource<Y> = Resource(status, function(data), error)

    companion object {
        internal fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null)
        internal fun <T> error(error: FrolloSDKError?, data: T? = null): Resource<T> = Resource(Status.ERROR, data, error)
    }
}