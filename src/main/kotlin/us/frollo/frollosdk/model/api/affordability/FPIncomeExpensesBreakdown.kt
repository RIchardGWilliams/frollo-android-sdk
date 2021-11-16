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

data class FPIncomeExpensesBreakdown(

    /**  The category in this breakdown; Optional */
    @SerializedName("category") val category: FPIncomeExpenseBreakdownCategory?,
    /**  Number of transactions in this breakdown; Optional */
    @SerializedName("number_of_transactions") val numberOfTransactions: Long?,
    /**  The date of the first Transaction in this breakdown; Optional */
    @SerializedName("first_transaction_date") val firstTransactionDate: String?,
    /**  The date of the last Transaction in this breakdown; Optional */
    @SerializedName("last_transaction_date") val lastTransactionDate: String?,
    /**  An object to repersent average of this breakdown; Optional */
    @SerializedName("averages") val averages: FPIncomeExpenseAverages?,
    /**  The [FPIncomeExpenseBreakdownTotals] in this breakdown; Optional */
    @SerializedName("totals") val FPIncomeExpenseBreakdownTotals: FPIncomeExpenseBreakdownTotals?,
    /**  An array of all Transactions that make up this expense breakdown. These can be used to query the Get Transactions API for more details on the underlying Transactions. */
    @SerializedName("transaction_ids") val transactionIds: List<Long>?
)
