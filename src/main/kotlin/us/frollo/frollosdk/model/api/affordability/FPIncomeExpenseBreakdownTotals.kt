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
import java.math.BigDecimal

data class FPIncomeExpenseBreakdownTotals(

    /**  The total amount spent in the last week of the reporting period. */
    @SerializedName("weekly") val weekly: BigDecimal?,
    /**  The total amount spent in the last month of the reporting period (if applicable). */
    @SerializedName("monthly") val monthly: BigDecimal?,
    /**  The total amount spent in the last 90 days of the reporting period (if applicable). */
    @SerializedName("ninety_day") val ninetyDay: BigDecimal?,
    /**  The total amount spent in the last 180 days of the reporting period (if applicable). */
    @SerializedName("one_eighty_day") val oneEightyDay: BigDecimal?,
    /** The total amount spent in the last year of the reporting period (if applicable). */
    @SerializedName("yearly") val yearly: BigDecimal?
)
