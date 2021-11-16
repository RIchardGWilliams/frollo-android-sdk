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
package us.frollo.frollosdk.model.api.financialpassport
import com.google.gson.annotations.SerializedName

data class IncomeExpensesBreakdown(

    @SerializedName("category") val category: Category,
    @SerializedName("number_of_transactions") val number_of_transactions: Int,
    @SerializedName("first_transaction_date") val first_transaction_date: String,
    @SerializedName("last_transaction_date") val last_transaction_date: String,
    @SerializedName("averages") val averages: Averages,
    @SerializedName("totals") val totals: Totals,
    @SerializedName("transaction_ids") val transaction_ids: List<Int>
)
