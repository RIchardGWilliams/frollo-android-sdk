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

package us.frollo.frollosdk.financialpassport

import okhttp3.ResponseBody
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.extensions.getFinancialPassport
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.model.api.financialpassport.ExportType
import us.frollo.frollosdk.model.api.financialpassport.FinancialPassportResponse
import us.frollo.frollosdk.model.api.statements.Statement
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.FinancialPassportAPI

/** Manages all aspects of FinancialPassport */
class Affordability(network: NetworkService) {

    companion object {
        private const val TAG = "FinancialPassport"
    }

    private val financialPassport: FinancialPassportAPI = network.create(FinancialPassportAPI::class.java)

    /**
     * Get financial passport from the host
     *
     * @param accountIds: List of  accounts IDs; Optional
     * @param providerAccountIDs: List of  provider accounts IDs; Optional
     * @param aggregator: `Provider.AggregatorType` type; Optional
     * @param fromDate: From date of financial passport; Optional; defaults to one year ago
     * @param toDate: To date of financial passport; Optional; defaults to today
     * @param completion: Completion handler with optional error if the request fails and list of [Statement] with pagination information if succeeds
     */
    fun getFinancialPassport(
        accountIds: List<Long>? = null,
        providerAccountIDs: List<Long>? = null,
        aggregator: AggregatorType? = null,
        fromDate: String? = null, // 2021-01-01
        toDate: String? = null, // 2021-01-01
        completion: OnFrolloSDKCompletionListener<Resource<FinancialPassportResponse>>
    ) {
        financialPassport.getFinancialPassport(
            accountIds,
            providerAccountIDs,
            aggregator,
            fromDate, // 2021-01-01
            toDate
        ).enqueue { resource ->
            if (resource.status == Resource.Status.ERROR) {
                Log.e("$TAG#getFinancialPassport", resource.error?.localizedDescription)
            }
            completion.invoke(resource)
        }
    }

    /**
     * Export financial passport from the host
     *
     * @param format of financial passport  to download
     * @param completion: Completion handler with optional error if the request fails and statement PDF body if succeeds
     */
    fun exportFinancialPassport(format: ExportType, completion: OnFrolloSDKCompletionListener<Resource<ResponseBody>>) {
        financialPassport.exportFinancialPassport(format).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    completion.invoke(resource)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#exportFinancialPassport", resource.error?.localizedDescription)
                    completion.invoke(resource)
                }
            }
        }
    }
}
