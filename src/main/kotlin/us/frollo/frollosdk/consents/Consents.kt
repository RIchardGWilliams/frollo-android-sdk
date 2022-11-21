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

package us.frollo.frollosdk.consents

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import us.frollo.frollosdk.base.PaginatedResult
import us.frollo.frollosdk.base.PaginationInfo
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.base.doAsync
import us.frollo.frollosdk.base.uiThread
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.extensions.fetchConsents
import us.frollo.frollosdk.extensions.fetchDisclosureConsents
import us.frollo.frollosdk.extensions.fetchExternalParties
import us.frollo.frollosdk.extensions.fetchProducts
import us.frollo.frollosdk.extensions.sqlForConsentIdsToGetStaleIds
import us.frollo.frollosdk.extensions.sqlForConsents
import us.frollo.frollosdk.extensions.sqlForDisclosureConsentIdsToGetStaleIds
import us.frollo.frollosdk.extensions.sqlForDisclosureConsents
import us.frollo.frollosdk.extensions.sqlForExternalParties
import us.frollo.frollosdk.extensions.sqlForExternalPartyIdsToGetStaleIds
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.mapping.toCDRConfiguration
import us.frollo.frollosdk.mapping.toConsent
import us.frollo.frollosdk.mapping.toConsentCreateRequest
import us.frollo.frollosdk.mapping.toConsentUpdateRequest
import us.frollo.frollosdk.mapping.toDisclosureConsent
import us.frollo.frollosdk.mapping.toExternalParty
import us.frollo.frollosdk.model.api.cdr.CDRConfigurationResponse
import us.frollo.frollosdk.model.api.cdr.ConsentResponse
import us.frollo.frollosdk.model.api.cdr.DisclosureConsentResponse
import us.frollo.frollosdk.model.api.cdr.ExternalPartyResponse
import us.frollo.frollosdk.model.coredata.aggregation.providers.CDRProduct
import us.frollo.frollosdk.model.coredata.aggregation.providers.CDRProductCategory
import us.frollo.frollosdk.model.coredata.cdr.CDRConfiguration
import us.frollo.frollosdk.model.coredata.cdr.Consent
import us.frollo.frollosdk.model.coredata.cdr.ConsentCreateForm
import us.frollo.frollosdk.model.coredata.cdr.ConsentRelation
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus
import us.frollo.frollosdk.model.coredata.cdr.ConsentUpdateForm
import us.frollo.frollosdk.model.coredata.cdr.DisclosureConsent
import us.frollo.frollosdk.model.coredata.cdr.ExternalParty
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyStatus
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyType
import us.frollo.frollosdk.model.coredata.cdr.TrustedAdvisorType
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.CdrAPI

/**
 * Manages all consents related data.
 */
class Consents(network: NetworkService, internal val db: SDKDatabase) {

    companion object {
        private const val TAG = "Consents"
    }

    private val cdrAPI: CdrAPI = network.create(CdrAPI::class.java)

    // Consents

    /**
     * Fetch consent by ID from the cache
     *
     * @param consentId Unique consent ID to fetch
     *
     * @return LiveData object of Resource<Consent> which can be observed using an Observer for future changes as well.
     */
    fun fetchConsent(consentId: Long): LiveData<Consent?> {
        return db.consents().load(consentId)
    }

    /**
     * Fetch consents from the cache
     *
     * @param providerId Filter by associated provider ID of the consent (optional)
     * @param providerAccountId Filter by associated provider account ID of the consent (optional)
     * @param status Filter by the status of the consent (optional)
     *
     * @return LiveData object of Resource<List<Consent> which can be observed using an Observer for future changes as well.
     */
    fun fetchConsents(
        providerId: Long? = null,
        providerAccountId: Long? = null,
        status: ConsentStatus? = null
    ): LiveData<List<Consent>> {
        return db.consents().loadByQuery(sqlForConsents(providerId = providerId, providerAccountId = providerAccountId, status = status))
    }

    /**
     * Advanced method to fetch consents by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches consents from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of Resource<List<Consent>> which can be observed using an Observer for future changes as well.
     */
    fun fetchConsents(query: SimpleSQLiteQuery): LiveData<List<Consent>> {
        return db.consents().loadByQuery(query)
    }

    /**
     * Fetch consent by ID from the cache along with other associated data.
     *
     * @param consentId Unique consent ID to fetch
     *
     * @return LiveData object of Resource<ConsentRelation> which can be observed using an Observer for future changes as well.
     */
    fun fetchConsentWithRelation(consentId: Long): LiveData<ConsentRelation?> {
        return db.consents().loadWithRelation(consentId)
    }

    /**
     * Fetch consents from the cache along with other associated data.
     *
     * @param providerId Filter by associated provider ID of the consent (optional)
     * @param providerAccountId Filter by associated provider account ID of the consent (optional)
     * @param status Filter by the status of the consent (optional)
     *
     * @return LiveData object of Resource<List<ConsentRelation> which can be observed using an Observer for future changes as well.
     */
    fun fetchConsentsWithRelation(
        providerId: Long? = null,
        providerAccountId: Long? = null,
        status: ConsentStatus? = null
    ): LiveData<List<ConsentRelation>> {
        return db.consents().loadByQueryWithRelation(
            sqlForConsents(providerId = providerId, providerAccountId = providerAccountId, status = status)
        )
    }

    /**
     * Advanced method to fetch consents by SQL query from the cache along with other associated data.
     *
     * @param query SimpleSQLiteQuery: Select query which fetches consents from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of Resource<List<ConsentRelation>> which can be observed using an Observer for future changes as well.
     */
    fun fetchConsentsWithRelation(query: SimpleSQLiteQuery): LiveData<List<ConsentRelation>> {
        return db.consents().loadByQueryWithRelation(query)
    }

    /**
     * Refresh consents from the host with pagination.
     *
     * @param status Filter consents by status (Optional)
     * @param providerId Filter consents by ID of the associated provider (Optional)
     * @param providerAccountId Filter consents by ID of the associated provider account (Optional)
     * @param before Consent ID to fetch before this consent (optional)
     * @param after Consent ID to fetch upto this consent (optional)
     * @param size Count of objects to returned from the API (page size) (optional)
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshConsentsWithPagination(
        status: ConsentStatus? = null,
        providerId: Long? = null,
        providerAccountId: Long? = null,
        after: Long? = null,
        before: Long? = null,
        size: Long? = null,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>? = null
    ) {
        cdrAPI.fetchConsents(
            status = status,
            providerId = providerId,
            providerAccountId = providerAccountId,
            after = after,
            before = before,
            size = size
        ).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshConsentsWithPagination", resource.error?.localizedDescription)
                    completion?.invoke(PaginatedResult.Error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    val response = resource.data
                    handleConsentsWithPaginationResponse(
                        response = response?.data,
                        status = status,
                        providerId = providerId,
                        providerAccountId = providerAccountId,
                        before = response?.paging?.cursors?.before?.toLong(),
                        after = response?.paging?.cursors?.after?.toLong(),
                        completion = completion
                    )
                }
            }
        }
    }

    /**
     * Refresh all available consents from the host.
     *
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshAllConsents(completion: OnFrolloSDKCompletionListener<Result>? = null) {
        refreshNextConsents {
            completion?.invoke(it)
        }
    }

    private fun refreshNextConsents(
        after: Long? = null,
        completion: OnFrolloSDKCompletionListener<Result>? = null
    ) {
        refreshConsentsWithPagination(after = after) { result ->
            when (result) {
                is PaginatedResult.Success -> {
                    result.paginationInfo?.let { paginationInfo ->
                        if (paginationInfo.after == null) {
                            completion?.invoke(Result.success())
                        } else {
                            refreshNextConsents(
                                after = paginationInfo.after,
                                completion = completion
                            )
                        }
                    }
                }
                is PaginatedResult.Error -> {
                    Log.e("$TAG#refreshNextConsents", result.error?.localizedDescription)
                    completion?.invoke(Result.error(result.error))
                }
            }
        }
    }

    /**
     * Refresh a specific consent by ID from the host
     *
     * @param consentId ID of the consent to fetch
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshConsent(consentId: Long, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        cdrAPI.fetchConsent(consentId).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshConsent", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    handleConsentResponse(response = resource.data, completion = completion)
                }
            }
        }
    }

    /**
     * Submits consent form for a specific provider
     *
     * NOTE: Use the same method to update sharing duration for a CDR Consent
     *
     * @param consentForm The form that will be submitted
     * @param completion Optional completion handler with optional error if the request fails else ID of the Consent created if success
     */
    fun submitConsent(consentForm: ConsentCreateForm, completion: OnFrolloSDKCompletionListener<Resource<Long>>? = null) {
        val request = consentForm.toConsentCreateRequest()
        cdrAPI.submitConsent(request).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#submitConsent", resource.error?.localizedDescription)
                    completion?.invoke(Resource.error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    // Since submitting a consent might affect other consents for the user, we need to refresh all of them
                    refreshNextConsents { // This call will anyways add the newly created consent to the cache. Hence no need to call handleConsentResponse.
                        when (it.status) {
                            Result.Status.SUCCESS -> {
                                completion?.invoke(Resource.success(resource.data?.consentId))
                            }
                            Result.Status.ERROR -> {
                                completion?.invoke(Resource.error(it.error))
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates consent form for a specific provider
     *
     * NOTE: Do not use this method to update sharing duration for a CDR Consent. Use [Consents.submitConsent] instead by passing the existing consent ID.
     *
     * @param consentId ID of the consent to be updated
     * @param consentForm The form that will be updated
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun updateConsent(consentId: Long, consentForm: ConsentUpdateForm, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        val request = consentForm.toConsentUpdateRequest()
        cdrAPI.updateConsent(consentId, request).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#updateConsent", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    handleConsentResponse(resource.data, completion)
                }
            }
        }
    }

    /**
     * Withdraws a consent deleting all its data
     *
     * @param consentId ID of the consent to be withdrawn
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun withdrawConsent(consentId: Long, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        val consentForm = ConsentUpdateForm(status = ConsentUpdateForm.ConsentUpdateStatus.WITHDRAWN)
        updateConsent(consentId, consentForm, completion)
    }

    /**
     * Updates a consent sharing period for YODLEE provider ONLY
     *
     * @param consentId ID of the consent to be updated
     * @param sharingDuration sharingDuration (in seconds) of the consent that will be updated. This duration will be added to the existing value by host.
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun updateConsentSharingPeriod(
        consentId: Long,
        sharingDuration: Long,
        completion: OnFrolloSDKCompletionListener<Result>? = null
    ) {
        val consentForm = ConsentUpdateForm(sharingDuration = sharingDuration)
        updateConsent(consentId, consentForm, completion)
    }

    private fun handleConsentsWithPaginationResponse(
        response: List<ConsentResponse>?,
        status: ConsentStatus? = null,
        providerId: Long? = null,
        providerAccountId: Long? = null,
        after: Long?,
        before: Long?,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>?
    ) {
        response?.let {
            doAsync {
                // Insert all consents from API response
                val models = response.map { it.toConsent() }
                db.consents().insertAll(*models.toTypedArray())

                // Fetch IDs from API response
                val apiIds = models.map { it.consentId }.toList()

                // Get IDs from database
                val consentIds = db.consents().getIdsByQuery(
                    sqlForConsentIdsToGetStaleIds(
                        status = status,
                        providerId = providerId,
                        providerAccountId = providerAccountId,
                        before = before,
                        after = after
                    )
                ).toHashSet()

                // Get stale IDs that are not present in the API response
                val staleIds = consentIds.minus(apiIds)

                if (staleIds.isNotEmpty()) {
                    db.consents().deleteMany(staleIds.toLongArray())
                }

                uiThread {
                    val paginationInfo = PaginationInfo(before = before, after = after)
                    completion?.invoke(PaginatedResult.Success(paginationInfo))
                }
            }
        } ?: run { completion?.invoke(PaginatedResult.Success()) } // Explicitly invoke completion callback if response is null.
    }

    private fun handleConsentResponse(response: ConsentResponse?, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        response?.let {
            doAsync {
                val model = response.toConsent()

                db.consents().insert(model)

                uiThread { completion?.invoke(Result.success()) }
            }
        } ?: run { completion?.invoke(Result.success()) } // Explicitly invoke completion callback if response is null.
    }

    // CDR Configuration

    /**
     * Fetch CDR Configuration from the cache for the tenant external ID
     *
     * @param externalId External Identifier to specify the tenant
     * @return LiveData object of Resource<CDRConfiguration> which can be observed using an Observer for future changes as well.
     */
    fun fetchCDRConfiguration(externalId: String): LiveData<CDRConfiguration?> {
        return db.cdrConfiguration().load(externalId)
    }

    /**
     * Refresh CDR Configuration for the tenant external ID from the host.
     *
     * @param externalId External Identifier to specify the tenant
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshCDRConfiguration(externalId: String, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        cdrAPI.fetchCDRConfig(externalId).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshCDRConfiguration", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    handleCDRConfigurationResponse(response = resource.data, completion = completion)
                }
            }
        }
    }

    private fun handleCDRConfigurationResponse(response: CDRConfigurationResponse?, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        response?.let {
            doAsync {
                db.cdrConfiguration().insert(response.toCDRConfiguration())
                db.cdrConfiguration().deleteStaleIds(response.configId)

                uiThread { completion?.invoke(Result.success()) }
            }
        } ?: run { completion?.invoke(Result.success()) } // Explicitly invoke completion callback if response is null.
    }

    // CDR Products

    /**
     * Fetch CDR products from server. CDR products can be filtered, sorted and ordered based on the parameters provided.
     *
     * @param providerId the ID of the provider to filter the products on. (Optional)
     * @param providerAccountId the ID of the provider account to filter the products on. (Optional)
     * @param accountId the ID of the account to filter the products on. (Optional)
     * @param productCategory Product Category to filter the products on. See [CDRProductCategory] for more details. (Optional)
     * @param productName Name of the product to filter the products on. (Optional)
     * @param completion Completion handler with optional error if the request fails and list of CDR Products if succeeds
     */
    fun fetchCDRProducts(
        providerId: Long? = null,
        providerAccountId: Long? = null,
        accountId: Long? = null,
        productCategory: CDRProductCategory? = null,
        productName: String? = null,
        completion: OnFrolloSDKCompletionListener<Resource<List<CDRProduct>>>
    ) {
        cdrAPI.fetchProducts(
            providerId = providerId,
            providerAccountId = providerAccountId,
            accountId = accountId,
            productCategory = productCategory,
            productName = productName
        ).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    completion.invoke(resource)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#fetchCDRProducts", resource.error?.localizedDescription)
                    completion.invoke(resource)
                }
            }
        }
    }

    /**
     * Fetch CDR product by ID from the server
     *
     * @param productId the ID of the product to filter the products on. (Optional)
     * @param completion Completion handler with optional error if the request fails and CDR Product if succeeds
     */
    fun fetchCDRProduct(productId: Long, completion: OnFrolloSDKCompletionListener<Resource<CDRProduct>>) {
        cdrAPI.fetchProduct(productId).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    completion.invoke(resource)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#fetchCDRProduct", resource.error?.localizedDescription)
                    completion.invoke(resource)
                }
            }
        }
    }

    // External Party

    /**
     * Fetch external party by ID from the cache
     *
     * @param externalPartyId Unique external party ID to fetch
     *
     * @return LiveData object of [ExternalParty] which can be observed using an Observer for future changes as well.
     */
    fun fetchExternalParty(externalPartyId: Long): LiveData<ExternalParty?> {
        return db.externalParty().load(externalPartyId)
    }

    /**
     * Fetch external parties from the cache
     *
     * @param externalIds Filter external parties to be refreshed by external IDs (Optional)
     * @param status Filter external parties to be refreshed by [ExternalPartyStatus] (Optional)
     * @param trustedAdvisorType Filter external parties to be refreshed by [TrustedAdvisorType] (Optional)
     * @param type Filter external parties to be refreshed by [ExternalPartyType] (Optional)
     * @param key Filter external parties to be refreshed by the generated unique key (Optional)
     *
     * @return LiveData object of List<ExternalParty> which can be observed using an Observer for future changes as well.
     */
    fun fetchExternalParties(
        externalIds: List<String>? = null,
        status: ExternalPartyStatus? = null,
        trustedAdvisorType: TrustedAdvisorType? = null,
        type: ExternalPartyType? = null,
        key: String? = null
    ): LiveData<List<ExternalParty>> {
        return db.externalParty().loadByQuery(
            sqlForExternalParties(externalIds, status, trustedAdvisorType, type, key)
        )
    }

    /**
     * Advanced method to fetch external parties by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches external parties from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of List<ExternalParty> which can be observed using an Observer for future changes as well.
     */
    fun fetchExternalParties(query: SimpleSQLiteQuery): LiveData<List<ExternalParty>> {
        return db.externalParty().loadByQuery(query)
    }

    /**
     * Refresh a specific external party by ID from the host
     *
     * @param externalPartyId ID of the external party to fetch
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshExternalParty(externalPartyId: Long, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        cdrAPI.fetchExternalParty(externalPartyId).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshExternalParty", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    handleExternalPartyResponse(response = resource.data, completion = completion)
                }
            }
        }
    }

    /**
     * Refresh All external parties
     *
     * @param externalIds Filter external parties to be refreshed by external IDs (Optional)
     * @param status Filter external parties to be refreshed by [ExternalPartyStatus] (Optional)
     * @param trustedAdvisorType Filter external parties to be refreshed by [TrustedAdvisorType] (Optional)
     * @param type Filter external parties to be refreshed by [ExternalPartyType] (Optional)
     * @param key Filter external parties to be refreshed by the generated unique key (Optional)
     * @param before before field to get previous list in pagination (Optional)
     * @param after after field to get next list in pagination (Optional)
     * @param size Count of objects to returned from the API (page size)
     * @param completion Optional completion handler with optional error if the request fails  else pagination data is success (Optional)
     */
    fun refreshExternalPartiesWithPagination(
        externalIds: List<String>? = null,
        status: ExternalPartyStatus? = null,
        trustedAdvisorType: TrustedAdvisorType? = null,
        type: ExternalPartyType? = null,
        key: String? = null,
        before: String? = null,
        after: String? = null,
        size: Long? = null,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>? = null
    ) {
        cdrAPI.fetchExternalParties(
            externalIds = externalIds,
            status = status,
            trustedAdvisorType = trustedAdvisorType,
            type = type,
            key = key,
            before = before,
            after = after,
            size = size
        ).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshExternalPartiesWithPagination", resource.error?.localizedDescription)
                    completion?.invoke(PaginatedResult.Error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    val response = resource.data
                    handleExternalPartiesWithPaginationResponse(
                        response = response?.data,
                        externalIds = externalIds,
                        status = status,
                        trustedAdvisorType = trustedAdvisorType,
                        type = type,
                        key = key,
                        before = response?.paging?.cursors?.before?.toLong(),
                        after = response?.paging?.cursors?.after?.toLong(),
                        completion = completion
                    )
                }
            }
        }
    }

    private fun handleExternalPartyResponse(
        response: ExternalPartyResponse?,
        completion: OnFrolloSDKCompletionListener<Result>? = null
    ) {
        response?.let {
            doAsync {
                val model = response.toExternalParty()

                db.externalParty().insert(model)

                uiThread {
                    completion?.invoke(Result.success())
                }
            }
        } ?: run {
            completion?.invoke(Result.success())
        } // Explicitly invoke completion callback if response is null.
    }

    private fun handleExternalPartiesWithPaginationResponse(
        response: List<ExternalPartyResponse>?,
        externalIds: List<String>? = null,
        status: ExternalPartyStatus? = null,
        trustedAdvisorType: TrustedAdvisorType? = null,
        type: ExternalPartyType? = null,
        key: String? = null,
        after: Long?,
        before: Long?,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>?
    ) {
        response?.let {
            doAsync {
                // Insert all external parties from API response
                val models = response.map { it.toExternalParty() }
                db.externalParty().insertAll(*models.toTypedArray())

                // Fetch IDs from API response
                val apiIds = response.map { it.partyId }.toHashSet()

                // Get IDs from database
                val externalPartyIds = db.externalParty().getIdsByQuery(
                    sqlForExternalPartyIdsToGetStaleIds(
                        before = before,
                        after = after,
                        externalIds = externalIds,
                        status = status,
                        trustedAdvisorType = trustedAdvisorType,
                        type = type,
                        key = key
                    )
                ).toHashSet()

                // Get stale IDs that are not present in the API response
                val staleIds = externalPartyIds.minus(apiIds)

                // Delete the entries for these stale IDs from database if they exist
                if (staleIds.isNotEmpty()) {
                    db.externalParty().deleteMany(staleIds.toLongArray())
                }

                uiThread {
                    val paginationInfo = PaginationInfo(before = before, after = after)
                    completion?.invoke(PaginatedResult.Success(paginationInfo))
                }
            }
        } ?: run { completion?.invoke(PaginatedResult.Success()) } // Explicitly invoke completion callback if response is null.
    }

    /**
     * Fetch disclosure consents from the cache
     *
     * @param status Filter disclosure consents to be refreshed by [ConsentStatus] (Optional)
     *
     * @return LiveData object of List<DisclosureConsent> which can be observed using an Observer for future changes as well.
     */
    fun fetchDisclosureConsents(
        status: ConsentStatus? = null,
    ): LiveData<List<DisclosureConsent>> {
        return db.disclosureConsent().loadByQuery(
            sqlForDisclosureConsents(status)
        )
    }

    /**
     * Advanced method to fetch disclosure consents by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches disclosure consents from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of List<DisclosureConsent> which can be observed using an Observer for future changes as well.
     */
    fun fetchDisclosureConsents(query: SimpleSQLiteQuery): LiveData<List<DisclosureConsent>> {
        return db.disclosureConsent().loadByQuery(query)
    }

    /**
     * Refresh All disclosure consents
     *
     * @param status Filter disclosure consents to be refreshed by [ConsentStatus] (Optional)
     * @param before before field to get previous list in pagination (Optional)
     * @param after after field to get next list in pagination (Optional)
     * @param size Count of objects to returned from the API (page size)
     * @param completion Optional completion handler with optional error if the request fails  else pagination data is success (Optional)
     */
    fun refreshDisclosureConsentsWithPagination(
        status: ConsentStatus? = null,
        before: String? = null,
        after: String? = null,
        size: Long? = null,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>? = null
    ) {
        cdrAPI.fetchDisclosureConsents(
            status = status,
            before = before,
            after = after,
            size = size
        ).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshDisclosureConsentsWithPagination", resource.error?.localizedDescription)
                    completion?.invoke(PaginatedResult.Error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    val response = resource.data
                    handleDisclosureConsentWithPaginationResponse(
                        response = response?.data,
                        status = status,
                        before = response?.paging?.cursors?.before?.toLong(),
                        after = response?.paging?.cursors?.after?.toLong(),
                        completion = completion
                    )
                }
            }
        }
    }

    private fun handleDisclosureConsentWithPaginationResponse(
        response: List<DisclosureConsentResponse>?,
        status: ConsentStatus? = null,
        after: Long?,
        before: Long?,
        size: Long? = null,
        completion: OnFrolloSDKCompletionListener<PaginatedResult<PaginationInfo>>? = null
    ) {
        response?.let {
            doAsync {
                // Insert all disclosure consents from API response
                val models = response.map { it.toDisclosureConsent() }
                db.disclosureConsent().insertAll(*models.toTypedArray())

                // Fetch IDs from API response
                val apiIds = response.map { it.consentId }.toHashSet()

                // Get IDs from database
                val disclosureConsentIds = db.disclosureConsent().getIdsByQuery(
                    sqlForDisclosureConsentIdsToGetStaleIds(
                        before = before,
                        after = after,
                        status = status,
                    )
                ).toHashSet()

                // Get stale IDs that are not present in the API response
                val staleIds = disclosureConsentIds.minus(apiIds)

                // Delete the entries for these stale IDs from database if they exist
                if (staleIds.isNotEmpty()) {
                    db.disclosureConsent().deleteMany(staleIds.toLongArray())
                }

                uiThread {
                    val paginationInfo = PaginationInfo(before = before, after = after)
                    completion?.invoke(PaginatedResult.Success(paginationInfo))
                }
            }
        } ?: run { completion?.invoke(PaginatedResult.Success()) } // Explicitly invoke completion callback if response is null.
    }
}
