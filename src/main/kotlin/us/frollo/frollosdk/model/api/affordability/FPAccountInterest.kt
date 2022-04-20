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

/** Data representation of the interest of Accounts of the Financial Passport */
data class FPAccountInterest(
    /** Interest rate percentage (Optional) */
    @SerializedName("rate") val rate: BigDecimal?,
    /** Interest type of an account (Optional) */
    @SerializedName("type") val type: FPInterestType?,
    /**
     * Expiry date of the interest rate (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("expiry_date") val expiryDate: String?,
    /** Interest earned over last 3 months (Optional) */
    @SerializedName("total_3_months") val totalThreeMonths: BigDecimal?,
    /** Interest earned over total period (Optional) */
    @SerializedName("total") val total: BigDecimal?
)
