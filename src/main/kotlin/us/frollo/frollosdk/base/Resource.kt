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

package us.frollo.frollosdk.base

import us.frollo.frollosdk.error.FrolloSDKError

/**
 * A value that represents either a success or a failure, including an associated data on success or error on failure.
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
    val error: FrolloSDKError? = null,
    /**
     * Response status code 202,200 etc
     */
    val responseStatusCode: Int? = null
) {

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
        /**
         * Instantiate Resource with status Success
         *
         * @param data Associated data (Optional)
         */
        fun <T> success(data: T?, responseStatusCode: Int? = null): Resource<T> = Resource(Status.SUCCESS, data, null, responseStatusCode)

        /**
         * Instantiate Resource with status Error
         *
         * @param error Associated error conforming [FrolloSDKError] (Optional)
         * @param data Associated data (Optional)
         */
        fun <T> error(error: FrolloSDKError?, data: T? = null): Resource<T> = Resource(Status.ERROR, data, error)
    }
}
