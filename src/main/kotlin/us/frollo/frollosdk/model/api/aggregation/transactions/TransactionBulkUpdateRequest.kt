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
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory

/**
 * Data Model for Transaction Bulk Update Request
 */
internal data class TransactionBulkUpdateRequest(

    /** ID of the Transaction */
    @SerializedName("id") val transactionId: Long,

    /** The ID of the category to recategorise the transaction to */
    @SerializedName("category_id") val categoryId: Long? = null,

    /** Budget category enum to change the transaction to */
    @SerializedName("budget_category") val budgetCategory: BudgetCategory? = null,

    /** Boolean indicating if the transaction is included or not */
    @SerializedName("included") val included: Boolean? = null,

    /** If true, we will create a new category rule for future transactions similar to the current transaction. Default is true. */
    @SerializedName("create_category_rule") val createCategoryRule: Boolean? = null,

    /** Boolean indicating if a budget category rule should be applied on future transactions. */
    @SerializedName("create_budget_category_rule") val createBudgetCategoryRule: Boolean? = null,

    /** Boolean indicating if an include/exclude rule should be applied on future transactions. */
    @SerializedName("create_include_rule") val createIncludeRule: Boolean? = null
)
