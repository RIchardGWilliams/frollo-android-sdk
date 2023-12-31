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

package us.frollo.frollosdk.model.coredata.aggregation.providers

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/** Aggregator with which a Provider get its data from */
enum class AggregatorType {

    /** Yodlee */
    @SerializedName("yodlee") YODLEE,

    /** OpenBanking - CDR */
    @SerializedName("cdr") CDR,

    /** Data Action */
    @SerializedName("data_action") DATA_ACTION,

    /** Demo */
    @SerializedName("demo") DEMO,

    /** Finpower */
    @SerializedName("finpower") FINPOWER,

    /** Frollo Score */
    @SerializedName("frollo_score") FROLLO_SCORE,

    /** Manual */
    @SerializedName("manual") MANUAL,

    /** Pioneer Credit Score */
    @SerializedName("pioneer_credit_score") PIONEER_CREDIT_SCORE,

    /** Pionner Credit Solutions */
    @SerializedName("pioneer_solutions") PIONEER_SOLUTIONS,

    /** Temenos T24 */
    @SerializedName("temenos") TEMENOS,

    /** VMA */
    @SerializedName("vma") VMA,

    /** Volt BAAP */
    @SerializedName("volt_baap") VOLT_BAAP,

    /** Unknown aggregator */
    @SerializedName("unknown") UNKNOWN;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
