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

/** Data representation of the recurring transaction of the Financial Passport */
data class FPRecurringTransaction(

    /** Name of recurring transaction (Optional) */
    @SerializedName("name") val name: String?,
    /** ID of the account of this transaction (Optional) */
    @SerializedName("account_id") val accountId: Long?,
    /** ID of the provider of this transaction (Optional) */
    @SerializedName("provider_id") val providerId: Long?,
    /** Frequency of occurrence (Optional) */
    @SerializedName("frequency") val frequency: FPFrequency?,
    /**
     * Start date of detected transactions (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("first_transaction_date") val firstTransactionDate: String?,
    /**
     * End date of detected transactions (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("last_transaction_date") val lastTransactionDate: String?,
    /** Irregular amount detected e.g. bonus (Optional) */
    @SerializedName("bonus") val bonus: BigDecimal?,
    /** Last 3 or more transaction amounts (Optional) */
    @SerializedName("last_transaction_amounts") val lastTransactionAmounts: List<BigDecimal>?,
    /** Average over just the latest 3 months (Optional) */
    @SerializedName("averages_3_months") val averagesThreeMonths: FPIncomeExpenseAverages?,
    /** Average over total period provided in query parameters (Optional) */
    @SerializedName("averages_total") val averagesTotal: FPIncomeExpenseAverages?,
    /** IDs of the recurring transactions (Optional) */
    @SerializedName("transaction_ids") val transactionIds: List<Long>?
)
