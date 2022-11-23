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

package us.frollo.frollosdk.extensions

import retrofit2.Call
import retrofit2.http.Query
import us.frollo.frollosdk.model.api.address.AddressAutocomplete
import us.frollo.frollosdk.model.api.affordability.FinancialPassportResponse
import us.frollo.frollosdk.model.api.aggregation.merchants.MerchantResponse
import us.frollo.frollosdk.model.api.aggregation.provideraccounts.ProviderAccountResponse
import us.frollo.frollosdk.model.api.aggregation.tags.TransactionTagResponse
import us.frollo.frollosdk.model.api.aggregation.transactions.TransactionResponse
import us.frollo.frollosdk.model.api.aggregation.transactions.TransactionsSummaryResponse
import us.frollo.frollosdk.model.api.bills.BillPaymentResponse
import us.frollo.frollosdk.model.api.budgets.BudgetPeriodResponse
import us.frollo.frollosdk.model.api.budgets.BudgetResponse
import us.frollo.frollosdk.model.api.cdr.ConsentResponse
import us.frollo.frollosdk.model.api.cdr.DisclosureConsentResponse
import us.frollo.frollosdk.model.api.cdr.ExternalPartyResponse
import us.frollo.frollosdk.model.api.contacts.ContactResponse
import us.frollo.frollosdk.model.api.goals.GoalResponse
import us.frollo.frollosdk.model.api.images.ImageResponse
import us.frollo.frollosdk.model.api.messages.MessageResponse
import us.frollo.frollosdk.model.api.reports.AccountBalanceReportResponse
import us.frollo.frollosdk.model.api.reports.CashflowBaseTypeReportResponse
import us.frollo.frollosdk.model.api.reports.CashflowReportResponse
import us.frollo.frollosdk.model.api.reports.ReportsResponse
import us.frollo.frollosdk.model.api.shared.PaginatedResponse
import us.frollo.frollosdk.model.api.statements.Statement
import us.frollo.frollosdk.model.api.statements.StatementSortBy
import us.frollo.frollosdk.model.api.statements.StatementType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountType
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType
import us.frollo.frollosdk.model.coredata.aggregation.providers.CDRProduct
import us.frollo.frollosdk.model.coredata.aggregation.providers.CDRProductCategory
import us.frollo.frollosdk.model.coredata.aggregation.transactions.ExportTransactionFilter
import us.frollo.frollosdk.model.coredata.aggregation.transactions.ExportTransactionType
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionFilter
import us.frollo.frollosdk.model.coredata.budgets.BudgetStatus
import us.frollo.frollosdk.model.coredata.budgets.BudgetType
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyStatus
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyType
import us.frollo.frollosdk.model.coredata.cdr.TrustedAdvisorType
import us.frollo.frollosdk.model.coredata.contacts.PaymentMethod
import us.frollo.frollosdk.model.coredata.goals.GoalStatus
import us.frollo.frollosdk.model.coredata.goals.GoalTrackingStatus
import us.frollo.frollosdk.model.coredata.managedproduct.ManagedProduct
import us.frollo.frollosdk.model.coredata.messages.MessageFilter
import us.frollo.frollosdk.model.coredata.reports.CashflowBaseType
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.reports.ReportPeriod
import us.frollo.frollosdk.model.coredata.reports.TransactionReportPeriod
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdk.model.coredata.shared.OrderType
import us.frollo.frollosdk.model.coredata.surveys.Survey
import us.frollo.frollosdk.network.api.AddressAPI
import us.frollo.frollosdk.network.api.AffordabilityAPI
import us.frollo.frollosdk.network.api.AggregationAPI
import us.frollo.frollosdk.network.api.BillsAPI
import us.frollo.frollosdk.network.api.BudgetsAPI
import us.frollo.frollosdk.network.api.CdrAPI
import us.frollo.frollosdk.network.api.ContactsAPI
import us.frollo.frollosdk.network.api.GoalsAPI
import us.frollo.frollosdk.network.api.ImagesAPI
import us.frollo.frollosdk.network.api.ManagedProductsAPI
import us.frollo.frollosdk.network.api.MessagesAPI
import us.frollo.frollosdk.network.api.ReportsAPI
import us.frollo.frollosdk.network.api.StatementsAPI
import us.frollo.frollosdk.network.api.SurveysAPI

// Aggregation

internal fun AggregationAPI.fetchTransactions(transactionFilter: TransactionFilter? = null): Call<PaginatedResponse<TransactionResponse>> {
    return fetchTransactions(transactionFilter?.getQueryMap() ?: mutableMapOf())
}

internal fun AggregationAPI.fetchSimilarTransactions(
    transactionId: Long,
    excludeSelf: Boolean? = null,
    fromDate: String? = null,
    toDate: String? = null,
    before: String? = null,
    after: String? = null,
    size: Long? = null
): Call<PaginatedResponse<TransactionResponse>> {

    val queryMap = mutableMapOf<String, String>()
    fromDate?.let { queryMap.put("from_date", it) }
    toDate?.let { queryMap.put("to_date", it) }
    excludeSelf?.let { queryMap.put("exclude_self", it.toString()) }
    before?.let { queryMap.put("before", it.toString()) }
    after?.let { queryMap.put("after", it.toString()) }
    size?.let { queryMap.put("size", it.toString()) }

    return fetchSimilarTransactions(transactionId, queryMap)
}

internal fun AggregationAPI.fetchTransactionsSummaryByQuery(
    fromDate: String, // yyyy-MM-dd
    toDate: String, // yyyy-MM-dd
    accountIds: LongArray? = null,
    accountIncluded: Boolean? = null,
    transactionIncluded: Boolean? = null
): Call<TransactionsSummaryResponse> {

    val queryMap = mutableMapOf("from_date" to fromDate, "to_date" to toDate)
    accountIncluded?.let { queryMap.put("account_included", it.toString()) }
    transactionIncluded?.let { queryMap.put("transaction_included", it.toString()) }
    accountIds?.let { queryMap.put("account_ids", it.joinToString(",")) }

    return fetchTransactionsSummary(queryMap)
}

internal fun AggregationAPI.fetchTransactionsSummaryByIDs(transactionIds: LongArray): Call<TransactionsSummaryResponse> =
    fetchTransactionsSummary(mapOf("transaction_ids" to transactionIds.joinToString(",")))

internal fun AggregationAPI.fetchMerchants(before: Long? = null, after: Long? = null, size: Long? = null, merchantIds: LongArray? = null): Call<PaginatedResponse<MerchantResponse>> {
    val queryMap = mutableMapOf<String, String>()
    before?.let { queryMap.put("before", it.toString()) }
    after?.let { queryMap.put("after", it.toString()) }
    size?.let { queryMap.put("size", it.toString()) }
    merchantIds?.let { queryMap.put("merchant_ids", it.joinToString(",")) }
    return fetchMerchants(queryMap)
}

internal fun AggregationAPI.exportTransactions(
    exportType: ExportTransactionType,
    exportTransactionFilter: ExportTransactionFilter? = null
): Call<Void> {
    val queryMap = mutableMapOf<String, String>()
    exportType.let { queryMap.put("type", it.toString()) }
    val filterMap = exportTransactionFilter?.getQueryMap()
    filterMap?.let {
        queryMap.putAll(it)
    }
    return exportTransactions(queryMap)
}

internal fun AggregationAPI.fetchUserTags(
    searchTerm: String? = null,
    sort: String? = null,
    order: String? = null
): Call<List<TransactionTagResponse>> {

    val queryMap = mutableMapOf<String, String>()
    searchTerm?.let { queryMap.put("search_term", it) }
    sort?.let { queryMap.put("sort", it) }
    order?.let { queryMap.put("order", it) }
    return fetchUserTags(queryMap)
}

internal fun AggregationAPI.fetchSuggestedTags(
    searchTerm: String? = null,
    sort: String? = null,
    order: String? = null
): Call<List<TransactionTagResponse>> {

    val queryMap = mutableMapOf<String, String>()
    searchTerm?.let { queryMap.put("search_term", it) }
    sort?.let { queryMap.put("sort", it) }
    order?.let { queryMap.put("order", it) }
    return fetchSuggestedTags(queryMap)
}

// Reports
internal fun ReportsAPI.fetchAccountBalanceReports(
    period: ReportPeriod,
    fromDate: String,
    toDate: String,
    accountId: Long? = null,
    accountType: AccountType? = null
): Call<AccountBalanceReportResponse> {

    val queryMap = mutableMapOf("period" to period.toString(), "from_date" to fromDate, "to_date" to toDate)
    accountType?.let { queryMap.put("container", it.toString()) }
    accountId?.let { queryMap.put("account_id", it.toString()) }
    return fetchAccountBalanceReports(queryMap)
}

internal fun ReportsAPI.fetchTransactionCategoryReports(
    period: TransactionReportPeriod,
    fromDate: String,
    toDate: String,
    categoryId: Long? = null,
    grouping: ReportGrouping? = null
): Call<ReportsResponse> {

    val queryMap = mutableMapOf("period" to period.toString(), "from_date" to fromDate, "to_date" to toDate)
    grouping?.let { queryMap.put("grouping", it.toString()) }
    return categoryId?.let { id ->
        fetchReportsByCategory(id, queryMap)
    } ?: fetchReportsByCategory(queryMap)
}

internal fun ReportsAPI.fetchMerchantReports(
    period: TransactionReportPeriod,
    fromDate: String,
    toDate: String,
    merchantId: Long? = null,
    grouping: ReportGrouping? = null
): Call<ReportsResponse> {

    val queryMap = mutableMapOf("period" to period.toString(), "from_date" to fromDate, "to_date" to toDate)
    grouping?.let { queryMap.put("grouping", it.toString()) }
    return merchantId?.let { id ->
        fetchReportsByMerchant(id, queryMap)
    } ?: fetchReportsByMerchant(queryMap)
}

internal fun ReportsAPI.fetchBudgetCategoryReports(
    period: TransactionReportPeriod,
    fromDate: String,
    toDate: String,
    budgetCategory: BudgetCategory? = null,
    grouping: ReportGrouping? = null
): Call<ReportsResponse> {

    val queryMap = mutableMapOf("period" to period.toString(), "from_date" to fromDate, "to_date" to toDate)
    grouping?.let { queryMap.put("grouping", it.toString()) }
    return budgetCategory?.let { bCategory ->
        fetchReportsByBudgetCategory(bCategory.budgetCategoryId, queryMap)
    } ?: fetchReportsByBudgetCategory(queryMap)
}

internal fun ReportsAPI.fetchTagReports(
    period: TransactionReportPeriod,
    fromDate: String,
    toDate: String,
    transactionTag: String? = null,
    grouping: ReportGrouping? = null
): Call<ReportsResponse> {

    val queryMap = mutableMapOf("period" to period.toString(), "from_date" to fromDate, "to_date" to toDate)
    grouping?.let { queryMap.put("grouping", it.toString()) }
    return transactionTag?.let { tag ->
        fetchReportsByTag(tag, queryMap)
    } ?: fetchReportsByTag(queryMap)
}

internal fun ReportsAPI.fetchCashflowReports(
    period: TransactionReportPeriod?,
    fromDate: String?,
    toDate: String?
): Call<CashflowReportResponse> {
    val queryMap = mutableMapOf<String, String>()
    period?.let { queryMap.put("period", it.toString()) }
    fromDate?.let { queryMap.put("from_date", it) }
    toDate?.let { queryMap.put("to_date", it) }
    return fetchCashflowReports(queryMap)
}

internal fun ReportsAPI.fetchCashflowReportsByBaseType(
    baseType: CashflowBaseType,
    period: TransactionReportPeriod?,
    fromDate: String?,
    toDate: String?,
    grouping: ReportGrouping?
): Call<CashflowBaseTypeReportResponse> {
    val queryMap = mutableMapOf<String, String>()
    period?.let { queryMap.put("period", it.toString()) }
    fromDate?.let { queryMap.put("from_date", it) }
    toDate?.let { queryMap.put("to_date", it) }
    grouping?.let { queryMap.put("grouping", it.toString()) }
    return fetchCashflowReportsByBaseType(baseType.toString(), queryMap)
}

// Bills

internal fun BillsAPI.fetchBillPayments(
    fromDate: String, // yyyy-MM-dd
    toDate: String, // yyyy-MM-dd
    skip: Int? = null,
    count: Int? = null
): Call<List<BillPaymentResponse>> {

    val queryMap = mutableMapOf("from_date" to fromDate, "to_date" to toDate)
    skip?.let { queryMap.put("skip", it.toString()) }
    count?.let { queryMap.put("count", it.toString()) }

    return fetchBillPayments(queryMap)
}

// Surveys

internal fun SurveysAPI.fetchSurvey(surveyKey: String, latest: Boolean? = null): Call<Survey> {
    val queryMap = mutableMapOf<String, String>()
    latest?.let { queryMap.put("latest", latest.toString()) }
    return fetchSurvey(surveyKey, queryMap)
}

// Goals

internal fun GoalsAPI.fetchGoals(status: GoalStatus? = null, trackingStatus: GoalTrackingStatus? = null): Call<List<GoalResponse>> {
    val queryMap = mutableMapOf<String, String>()
    status?.let { queryMap.put("status", status.toString()) }
    trackingStatus?.let { queryMap.put("tracking_status", trackingStatus.toString()) }
    return fetchGoals(queryMap)
}

// Budgets

internal fun BudgetsAPI.fetchBudgets(
    current: Boolean? = null,
    budgetType: BudgetType? = null
): Call<List<BudgetResponse>> {
    val queryMap = mutableMapOf<String, String>()
    current?.let { queryMap["current"] = it.toString() }
    budgetType?.let { queryMap["category_type"] = it.toString() }
    return fetchBudgets(queryMap)
}

internal fun BudgetsAPI.fetchBudgetPeriods(
    budgetId: Long? = null,
    budgetStatus: BudgetStatus? = null,
    fromDate: String? = null,
    toDate: String? = null,
    before: String? = null,
    after: String? = null,
    size: Long? = null
): Call<PaginatedResponse<BudgetPeriodResponse>> {
    val queryMap = mutableMapOf<String, String>()
    fromDate?.let { queryMap["from_date"] = it }
    toDate?.let { queryMap["to_date"] = it }
    after?.let { queryMap.put("after", it) }
    before?.let { queryMap.put("before", it) }
    size?.let { queryMap.put("size", it.toString()) }
    return budgetId?.let {
        fetchBudgetPeriodsByBudgetID(budgetId = it, queryMap)
    } ?: run {
        budgetStatus?.let { queryMap["status"] = it.toString() } // This query parameter is supported only for fetchAllBudgetPeriods API
        fetchAllBudgetPeriods(queryMap)
    }
}

// Images

internal fun ImagesAPI.fetchImages(imageType: String? = null): Call<List<ImageResponse>> {
    val queryMap = mutableMapOf<String, String>()
    imageType?.let { queryMap["image_type"] = it }
    return fetchImages(queryMap)
}

// CDR Products

internal fun CdrAPI.fetchProducts(
    providerId: Long? = null,
    providerAccountId: Long? = null,
    accountId: Long? = null,
    productCategory: CDRProductCategory? = null,
    productName: String? = null
): Call<List<CDRProduct>> {
    val queryMap = mutableMapOf<String, String>()
    providerId?.let { queryMap["provider_id"] = it.toString() }
    providerAccountId?.let { queryMap["provider_account_id"] = it.toString() }
    accountId?.let { queryMap["account_id"] = it.toString() }
    productCategory?.let { queryMap["product_category"] = it.toString() }
    productName?.let { queryMap["name"] = it }
    return fetchProducts(queryMap)
}

internal fun CdrAPI.fetchConsents(
    status: ConsentStatus? = null,
    providerId: Long? = null,
    providerAccountId: Long? = null,
    after: Long? = null,
    before: Long? = null,
    size: Long? = null
): Call<PaginatedResponse<ConsentResponse>> {
    val queryMap = mutableMapOf<String, String>()
    status?.let { queryMap.put("status", status.toString()) }
    providerId?.let { queryMap["provider_id"] = it.toString() }
    providerAccountId?.let { queryMap["provider_account_id"] = it.toString() }
    after?.let { queryMap.put("after", it.toString()) }
    before?.let { queryMap.put("before", it.toString()) }
    size?.let { queryMap.put("size", it.toString()) }
    return fetchConsents(queryMap)
}

// Contacts

internal fun ContactsAPI.fetchContacts(
    paymentMethod: PaymentMethod? = null,
    after: Long? = null,
    before: Long? = null,
    size: Long? = null
): Call<PaginatedResponse<ContactResponse>> {
    val queryMap = mutableMapOf<String, String>()
    paymentMethod?.let { queryMap["type"] = paymentMethod.toString() }
    after?.let { queryMap.put("after", it.toString()) }
    before?.let { queryMap.put("before", it.toString()) }
    size?.let { queryMap.put("size", it.toString()) }
    return fetchContacts(queryMap)
}

// Managed Products

internal fun ManagedProductsAPI.fetchAvailableProducts(
    after: Long? = null,
    before: Long? = null,
    size: Long? = null
): Call<PaginatedResponse<ManagedProduct>> {
    val queryMap = mutableMapOf<String, String>()
    after?.let { queryMap.put("after", it.toString()) }
    before?.let { queryMap.put("before", it.toString()) }
    size?.let { queryMap.put("size", it.toString()) }
    return fetchAvailableProducts(queryMap)
}

internal fun ManagedProductsAPI.fetchManagedProducts(
    after: Long? = null,
    before: Long? = null,
    size: Long? = null
): Call<PaginatedResponse<ManagedProduct>> {
    val queryMap = mutableMapOf<String, String>()
    after?.let { queryMap.put("after", it.toString()) }
    before?.let { queryMap.put("before", it.toString()) }
    size?.let { queryMap.put("size", it.toString()) }
    return fetchManagedProducts(queryMap)
}

internal fun AddressAPI.fetchSuggestedAddresses(query: String, max: Int): Call<List<AddressAutocomplete>> {
    val queryMap = mutableMapOf<String, String>()
    queryMap["query"] = query
    queryMap["max"] = max.toString()
    return fetchSuggestedAddresses(queryMap)
}

internal fun AggregationAPI.refreshProviderAccounts(providerAccountIds: LongArray): Call<List<ProviderAccountResponse>> {
    val queryMap = mutableMapOf<String, String>()
    queryMap["provideraccount_ids"] = providerAccountIds.joinToString(",")
    return refreshProviderAccounts(queryMap)
}

internal fun StatementsAPI.fetchStatements(
    accountIds: List<Long>,
    @Query("type")statementType: StatementType? = null,
    @Query("from_date")fromDate: String, // 2021-01-01
    @Query("to_date")toDate: String? = null, // 2021-01-01
    @Query("before")before: Int? = null,
    @Query("after")after: Int? = null,
    @Query("size")size: Int? = null,
    @Query("sort")statementSortBy: StatementSortBy? = null,
    @Query("order")orderType: OrderType? = null
): Call<PaginatedResponse<Statement>> {
    val queryMap = mutableMapOf<String, String>()
    queryMap["account_ids"] = accountIds.joinToString(",")
    statementType?.let {
        queryMap["type"] = it.toString()
    }
    queryMap["from_date"] = fromDate
    toDate?.let { queryMap["to_date"] = it }
    before?.let { queryMap["before"] = it.toString() }
    after?.let { queryMap["after"] = it.toString() }
    size?.let { queryMap["size"] = it.toString() }
    statementSortBy?.let { queryMap["sort"] = it.toString() }
    orderType?.let { queryMap["order"] = it.toString() }
    return fetchStatements(queryMap)
}

internal fun AffordabilityAPI.getFinancialPassport(
    accountIds: List<Long>? = null,
    providerAccountIDs: List<Long>? = null,
    aggregators: List<AggregatorType>? = null,
    fromDate: String? = null, // 2020-09-09
    toDate: String? = null, // 2020-09-09
): Call<FinancialPassportResponse> {
    val queryMap = mutableMapOf<String, String>()
    accountIds?.let {
        queryMap["account_ids"] = accountIds.joinToString(",")
    }
    providerAccountIDs?.let {
        queryMap["provider_account_ids"] = it.joinToString(",")
    }
    aggregators?.let {
        queryMap["aggregators"] = it.joinToString(",")
    }
    fromDate?.let {
        queryMap["from_date"] = it
    }
    toDate?.let {
        queryMap["to_date"] = it
    }
    return getFinancialPassport(queryMap)
}

internal fun MessagesAPI.fetchMessages(
    messageFilter: MessageFilter? = null
): Call<PaginatedResponse<MessageResponse>> {
    return fetchMessages(messageFilter?.getQueryMap() ?: mutableMapOf())
}

internal fun MessagesAPI.deleteMessagesInBulk(
    messageIds: List<Long>
): Call<Void> {
    val queryMap = mutableMapOf<String, String>()
    queryMap["message_ids"] = messageIds.joinToString(",")
    return deleteMessagesInBulk(queryMap)
}

internal fun CdrAPI.fetchExternalParties(
    externalIds: List<String>? = null,
    status: ExternalPartyStatus? = null,
    trustedAdvisorType: TrustedAdvisorType? = null,
    type: ExternalPartyType? = null,
    key: String? = null,
    before: String? = null,
    after: String? = null,
    size: Long? = null
): Call<PaginatedResponse<ExternalPartyResponse>> {
    val queryMap = mutableMapOf<String, String>()
    after?.let { queryMap.put("after", it) }
    before?.let { queryMap.put("before", it) }
    size?.let { queryMap.put("size", it.toString()) }
    externalIds?.let { queryMap["external_ids"] = it.joinToString(",") }
    status?.let { queryMap["status"] = it.toString() }
    trustedAdvisorType?.let { queryMap["ta_type"] = it.toString() }
    type?.let { queryMap["type"] = it.toString() }
    key?.let { queryMap["key"] = it }
    return fetchExternalParties(queryMap)
}

internal fun CdrAPI.fetchDisclosureConsents(
    status: ConsentStatus? = null,
    before: String? = null,
    after: String? = null,
    size: Long? = null
): Call<PaginatedResponse<DisclosureConsentResponse>> {
    val queryMap = mutableMapOf<String, String>()
    after?.let { queryMap.put("after", it) }
    before?.let { queryMap.put("before", it) }
    size?.let { queryMap.put("size", it.toString()) }
    status?.let { queryMap["status"] = it.toString() }
    return fetchDisclosureConsents(queryMap)
}
