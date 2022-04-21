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

/** Data representation of the breakdown of Income or expenses of the Financial Passport */
data class FPIncomeExpensesBreakdown(

    /** The category in this breakdown (Optional) */
    @SerializedName("category") val category: FPIncomeExpenseBreakdownCategory?,
    /**
     * First date of transactions in the this breakdown (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("first_transaction_date") val firstTransactionDate: String?,
    /**
     * Last date of transactions in the this breakdown (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("last_transaction_date") val lastTransactionDate: String?,
    /** An object to represent average of this breakdown (Optional) */
    @SerializedName("averages") val averages: FPIncomeExpenseAverages?,
    /**
     * An array of all Transactions that make up this expense breakdown.
     * These can be used to query the Get Transactions API for more details on the underlying Transactions. (Optional)
     */
    @SerializedName("transaction_ids") val transactionIds: List<Long>?,
    /** Recurring Transactions. Only returned for Salary, Other Income and Deposit. (Optional) */
    @SerializedName("recurring_transactions") val recurringTransactions: List<FPRecurringTransaction>?
)
