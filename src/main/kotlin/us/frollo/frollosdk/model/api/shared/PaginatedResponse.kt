/*
 * Copyright 2020 Frollo
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

package us.frollo.frollosdk.model.api.shared

import com.google.gson.annotations.SerializedName

/**
 * Response with information of pagination
 *
 * @param data Array of data items
 * @param paging Pagination information
 */
internal data class PaginatedResponse<T>(
    @SerializedName("data") val data: List<T>,
    @SerializedName("paging") val paging: Paging
) {

    /**
     * Pagination Information
     *
     * @param cursors Page cursors
     * @param previous Previous Page URL
     * @param next Next Page URL
     * @param total Total number of data items in all the pages
     */
    class Paging(
        @SerializedName("cursors") val cursors: PagingCursors?,
        @SerializedName("previous") val previous: String?,
        @SerializedName("next") val next: String?,
        @SerializedName("total") val total: Long?
    )

    /**
     * Page Cursors
     *
     * @param before Before cursor
     * @param after After cursor
     */
    class PagingCursors(
        @SerializedName("before") var before: String?,
        @SerializedName("after") var after: String?
    )
}
