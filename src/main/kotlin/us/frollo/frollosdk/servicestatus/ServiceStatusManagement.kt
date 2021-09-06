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

package us.frollo.frollosdk.servicestatus

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.base.doAsync
import us.frollo.frollosdk.base.uiThread
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.extensions.sqlForExistingOutage
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.mapping.toServiceOutage
import us.frollo.frollosdk.mapping.update
import us.frollo.frollosdk.model.api.servicestatus.ServiceOutageResponse
import us.frollo.frollosdk.model.api.servicestatus.ServiceStatusResponse
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutage
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.ServiceStatusAPI

/** Manages all aspects of Service Status & Outages */
class ServiceStatusManagement(network: NetworkService, internal val db: SDKDatabase) {

    companion object {
        private const val TAG = "Service Status Management"
    }

    private val serviceStatusAPI: ServiceStatusAPI = network.createExternalNoAuth(ServiceStatusAPI::class.java)

    /**
     * Fetch service status from the host
     *
     * @param url URL for service status API. It is different on environments & tenants.
     * @param completion Completion handler with error if the request fails or [ServiceStatusResponse] if success
     */
    fun fetchServiceStatus(url: String, completion: OnFrolloSDKCompletionListener<Resource<ServiceStatusResponse>>) {
        serviceStatusAPI.fetchServiceStatus(url).enqueue { resource ->
            if (resource.status == Resource.Status.ERROR) {
                Log.e("$TAG#fetchServiceStatus", resource.error?.localizedDescription)
            }
            completion.invoke(resource)
        }
    }

    /**
     * Fetch all outages from the cache
     *
     * @return LiveData object of List<ServiceOutage> which can be observed using an Observer for future changes as well.
     */
    fun fetchServiceOutages(): LiveData<List<ServiceOutage>> {
        return db.serviceOutages().load()
    }

    /**
     * Advanced method to fetch outages by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches outages from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of List<ServiceOutage> which can be observed using an Observer for future changes as well.
     */
    fun fetchServiceOutages(query: SimpleSQLiteQuery): LiveData<List<ServiceOutage>> {
        return db.serviceOutages().loadByQuery(query)
    }

    /**
     * Refresh all available outages from the host.
     *
     * @param url URL for service outages API. It is different on environments & tenants.
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun refreshServiceOutages(url: String, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        serviceStatusAPI.fetchServiceOutages(url).enqueue { resource ->
            when (resource.status) {
                Resource.Status.ERROR -> {
                    Log.e("$TAG#refreshServiceOutages", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
                Resource.Status.SUCCESS -> {
                    handleOutagesResponse(resource.data, completion)
                }
            }
        }
    }

    /**
     * Update outage message after user read the message
     *
     * @param outageId ID of the outage to be marked as read
     * @param completion Optional completion handler with optional error if the request fails
     */
    fun markOutageAsRead(outageId: Long, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        doAsync {
            db.serviceOutages().markOutageAsRead(outageId)
            uiThread { completion?.invoke(Result.success()) }
        }
    }

    private fun handleOutagesResponse(
        response: List<ServiceOutageResponse>?,
        completion: OnFrolloSDKCompletionListener<Result>?
    ) {
        if (response == null) {
            completion?.invoke(Result.success()) // Explicitly invoke completion callback if response is null.
            return
        }

        val modelsToInsert = mutableListOf<ServiceOutage>()
        val modelsToUpdate = mutableListOf<ServiceOutage>()

        doAsync {
            response.forEach { outageResponse ->
                // Find first existing outage message which matches - type, start date & end date
                val existingOutage = db.serviceOutages().find(
                    sqlForExistingOutage(
                        type = outageResponse.type,
                        startDate = outageResponse.startDate,
                        endDate = outageResponse.endDate
                    )
                ).firstOrNull()
                val model = outageResponse.toServiceOutage()
                existingOutage?.let { // Update existing outage if found
                    it.update(model)
                    modelsToUpdate.add(it)
                } ?: run { // Insert new outage if doesn't exist already
                    modelsToInsert.add(model)
                }
            }

            // Delete stale outages (IDs that are not in modelsToUpdate) before updating existing / inserting new
            val idsToUpdate = modelsToUpdate.map { it.outageId }
            val cachedIds = db.serviceOutages().getIds()
            val staleIds = cachedIds.minus(idsToUpdate)
            db.serviceOutages().deleteMany(staleIds.toLongArray())

            // Update existing
            db.serviceOutages().update(*modelsToUpdate.toTypedArray())
            // Insert new
            db.serviceOutages().insertAll(*modelsToInsert.toTypedArray())

            uiThread { completion?.invoke(Result.success()) }
        }
    }
}
