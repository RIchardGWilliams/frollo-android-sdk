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

package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName

/** Data representation of the Liabilities of the Financial Passport */
data class FPLiabilities(

    /** The total value of liabilities over the report period (Optional) */
    @SerializedName("summary") val summary: FPLiabilitySummary?,

    /** The breakdown of liabilities over the report period (Optional) */
    @SerializedName("breakdown") val breakdown: List<FPLiabilityBreakdown>?
)
