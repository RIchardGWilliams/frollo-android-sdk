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

package us.frollo.frollosdk.model.api.aggregation.transactions

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionBaseType
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionStatus
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionType
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import java.math.BigDecimal

internal data class ManualTransactionCreateUpdateRequest(
    @SerializedName("account_id") val accountId: Long,
    @SerializedName("base_type") val baseType: TransactionBaseType,
    @SerializedName("transaction_status") val transactionStatus: TransactionStatus,
    @SerializedName("included") val included: Boolean,
    @SerializedName("transaction_date") val transactionDate: String,
    @SerializedName("posted_date") val postedDate: String?,
    @SerializedName("category_id") val categoryId: Long?,
    @SerializedName("merchant_id") val merchantId: Long?,
    @SerializedName("merchant_location_id") val merchantLocationId: Long?,
    @SerializedName("budget_category") val budgetCategory: BudgetCategory?,
    @SerializedName("memo") val memo: String?,
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("original_description") val originalDescription: String,
    @SerializedName("payee") val payee: String?,
    @SerializedName("payer") val payer: String?,
    @SerializedName("reference") val reference: String?,
    @SerializedName("type") val type: TransactionType,
)
