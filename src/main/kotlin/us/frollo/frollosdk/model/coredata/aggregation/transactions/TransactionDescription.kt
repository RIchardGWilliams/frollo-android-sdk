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

package us.frollo.frollosdk.model.coredata.aggregation.transactions

import com.google.gson.annotations.SerializedName

/** Transaction description */
data class TransactionDescription(

    /** Original description of the transaction */
    @SerializedName("original") val original: String,

    /** User determined description of the transaction (optional) */
    @SerializedName("user") var user: String? = null,

    /** Simplified description of the transaction (optional) */
    @SerializedName("simple") val simple: String? = null
)
