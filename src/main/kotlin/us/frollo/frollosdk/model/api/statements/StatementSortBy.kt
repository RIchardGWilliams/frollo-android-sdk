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

package us.frollo.frollosdk.model.api.statements

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

@Keep
/** The order by which the statements should be sorted while fetching the statement list */
enum class StatementSortBy {

    /** Sort statements by statement start date*/
    @SerializedName("period_start_date") PERIOD_START_DATE,
    /** Sort statements by statement end date*/
    @SerializedName("period_end_date")PERIOD_END_DATE,
    /** Sort statements by account number */
    @SerializedName("account_number")ACCOUNT_NUMBER,
    /** Sort statements by statement type [StatementType]*/
    @SerializedName("type")TYPE;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
