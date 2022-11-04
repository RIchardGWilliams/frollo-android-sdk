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

package us.frollo.frollosdk.model.coredata.aggregation.transactions

import us.frollo.frollosdk.model.coredata.aggregation.merchants.MerchantDetails
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory

/**
 * Represents a model that contains all the filters to apply on exported transactions
 *
 * @param accountAggregator Filters by the linked account's aggregator (Optional)
 * @param accountIds List of [Transaction.accountId] to filter transactions (Optional)
 * @param accountIncluded 'included' status of 'Account' to filter by (Optional)
 * @param baseType [TransactionBaseType] to filter transactions (Optional)
 * @param billId billID to filter the associated transactions (Optional)
 * @param budgetCategory [BudgetCategory] to filter transactions on (Optional)
 * @param count Max number of records in the response (Optional)
 * @param fields List of Columns to be exported (Optional)
 * @param fromDate Date to filter transactions from (inclusive). Please use [Transaction.DATE_FORMAT_PATTERN] for the format pattern. (Optional)
 * @param goalId goalID to filter the associated transactions (Optional)
 * @param maximumAmount Amount to filter transactions to (inclusive) (Optional)
 * @param merchantIds List of [MerchantDetails.id] to filter transactions (Optional)
 * @param minimumAmount Amount to filter transactions from (inclusive) (Optional)
 * @param payday Filters only the transactions that are linked to the salary (Optional)
 * @param searchTerm Search term to filter transactions (Optional)
 * @param status [TransactionStatus] to filter transactions (Optional)
 * @param tags List of tags to filter transactions (Optional)
 * @param toDate Date to filter transactions to (inclusive). Please use [Transaction.DATE_FORMAT_PATTERN] for the format pattern. (Optional)
 * @param transactionCategoryIds List of [Transaction.category.id] to filter transactions (Optional)
 * @param transactionIds List of [Transaction.transactionId] to filter transactions (Optional)
 * @param transactionIncluded [Transaction.included] status of 'Transaction' to filter by (Optional)
 **/
data class ExportTransactionFilter(
    var accountAggregator: AggregatorType? = null,
    var accountIds: List<Long>? = null,
    var accountIncluded: Boolean? = null,
    var baseType: TransactionBaseType? = null,
    var billId: Long? = null,
    var budgetCategory: BudgetCategory? = null,
    var count: Long? = null,
    var fields: List<ExportTransactionField>? = null,
    var fromDate: String? = null, // yyyy-MM-dd
    var goalId: Long? = null,
    var maximumAmount: String? = null,
    var merchantIds: List<Long>? = null,
    var minimumAmount: String? = null,
    var payday: Boolean? = null,
    var searchTerm: String? = null,
    var status: TransactionStatus? = null,
    var tags: List<String>? = null,
    var toDate: String? = null, // yyyy-MM-dd
    var transactionCategoryIds: List<Long>? = null,
    var transactionIds: List<Long>? = null,
    var transactionIncluded: Boolean? = null
) {

    /**
     * Convert [ExportTransactionFilter] to query map
     */
    fun getQueryMap(): Map<String, String> {
        val queryMap = mutableMapOf<String, String>()
        accountAggregator?.let { queryMap.put("account_aggregator", it.toString()) }
        accountIds?.let { if (it.isNotEmpty()) queryMap.put("account_ids", it.joinToString(",")) }
        accountIncluded?.let { queryMap.put("account_included", it.toString()) }
        baseType?.let { queryMap.put("base_type", it.toString()) }
        billId?.let { queryMap.put("bill_id", it.toString()) }
        budgetCategory?.let { queryMap.put("budget_category", it.toString()) }
        count?.let { queryMap.put("count", it.toString()) }
        fields?.let { queryMap.put("fields", it.joinToString(",")) }
        fromDate?.let { if (it.isNotBlank()) queryMap.put("from_date", it) else null }
        goalId?.let { queryMap.put("goal_id", it.toString()) }
        maximumAmount?.let { if (it.isNotBlank()) queryMap.put("max_amount", it) else null }
        merchantIds?.let { if (it.isNotEmpty()) queryMap.put("merchant_ids", it.joinToString(",")) }
        minimumAmount?.let { if (it.isNotBlank()) queryMap.put("min_amount", it) else null }
        payday?.let { queryMap.put("pay_day", it.toString()) }
        searchTerm?.let { if (it.isNotBlank()) queryMap.put("search_term", it) else null }
        status?.let { queryMap.put("status", it.toString()) }
        tags?.let { if (it.isNotEmpty()) queryMap.put("tags", it.joinToString(",")) }
        toDate?.let { if (it.isNotBlank()) queryMap.put("to_date", it) else null }
        transactionCategoryIds?.let { if (it.isNotEmpty()) queryMap.put("transaction_category_ids", it.joinToString(",")) }
        transactionIds?.let { if (it.isNotEmpty()) queryMap.put("transaction_ids", it.joinToString(",")) }
        transactionIncluded?.let { queryMap.put("transaction_included", it.toString()) }
        return queryMap
    }
}
