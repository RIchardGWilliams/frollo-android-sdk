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

package us.frollo.frollosdk.affordability

import com.google.gson.Gson
import okhttp3.ResponseBody
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.R
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.core.OnFrolloSDKExportDataCompletionListener
import us.frollo.frollosdk.core.OnFrolloSDKFPCompletionListener
import us.frollo.frollosdk.error.FrolloSDKError
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.extensions.fetchNetworth
import us.frollo.frollosdk.extensions.fromJson
import us.frollo.frollosdk.extensions.getFinancialPassport
import us.frollo.frollosdk.extensions.readStringFromJson
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.model.api.affordability.AssetsLiabilitiesResponse
import us.frollo.frollosdk.model.api.affordability.ExportType
import us.frollo.frollosdk.model.api.affordability.FinancialPassportResponse
import us.frollo.frollosdk.model.api.affordability.NetworthResponse
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.AffordabilityAPI

/** Manages all aspects of Affordability */
class Affordability(network: NetworkService) {

    companion object {
        private const val TAG = "Affordability"
    }

    private val affordabilityAPI: AffordabilityAPI = network.create(AffordabilityAPI::class.java)

    /**
     * Get financial passport from the host
     *
     * @param accountIds: List of  accounts IDs; Optional
     * @param providerAccountIDs: List of  provider accounts IDs; Optional
     * @param aggregators: List of [AggregatorType] types; Optional
     * @param fromDate: From date of financial passport; Optional; defaults to one year ago. See [FinancialPassportResponse.DATE_FORMAT_PATTERN]
     * @param toDate: To date of financial passport; Optional; defaults to today . See [FinancialPassportResponse.DATE_FORMAT_PATTERN]
     * @param completion: Completion handler with optional error if the request fails or [FinancialPassportResponse] if succeeds
     */
    fun getFinancialPassport(
        accountIds: List<Long>? = null,
        providerAccountIDs: List<Long>? = null,
        aggregators: List<AggregatorType>? = null,
        fromDate: String? = null, // 2021-01-01
        toDate: String? = null, // 2021-01-01
        completion: OnFrolloSDKFPCompletionListener<Boolean, Resource<FinancialPassportResponse>?>
    ) {
        affordabilityAPI.getFinancialPassport(
            accountIds,
            providerAccountIDs,
            aggregators,
            fromDate, // 2021-01-01
            toDate
        ).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    when (resource.responseStatusCode) {
                        202 -> completion.invoke(true, null)
                        else -> completion.invoke(false, resource)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#getFinancialPassport", resource.error?.localizedDescription)
                    completion.invoke(false, resource)
                }
            }
        }
    }

    /**
     * Export financial passport from the host
     *
     * @param format of financial passport  to download
     * @param completion: Completion handler with optional error if the request fails or flag indicating request is still processing & Financial Passport PDF body if succeeds
     */
    fun exportFinancialPassport(format: ExportType, completion: OnFrolloSDKExportDataCompletionListener<Boolean, Resource<ResponseBody>>) {
        affordabilityAPI.exportFinancialPassport(format).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    when (resource.responseStatusCode) {
                        202 -> completion.invoke(true, null)
                        else -> completion.invoke(false, resource)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#exportFinancialPassport", resource.error?.localizedDescription)
                    completion.invoke(false, resource)
                }
            }
        }
    }

    /**
     * Get net worth from the host
     *
     * @param accountIds: List of  accounts IDs; Optional
     * @param providerAccountIDs: List of  provider accounts IDs; Optional
     * @param aggregators: List of [AggregatorType] types; Optional
     * @param completion: Completion handler with optional error if the request fails or [NetworthResponse] if succeeds
     */
    fun fetchNetworth(
        accountIds: List<Long>? = null,
        providerAccountIDs: List<Long>? = null,
        aggregators: List<AggregatorType>? = null,
        completion: OnFrolloSDKCompletionListener<Resource<NetworthResponse>>
    ) {
        affordabilityAPI.fetchNetworth(
            accountIds,
            providerAccountIDs,
            aggregators,
        ).enqueue { resource ->
            if (resource.status == Resource.Status.ERROR) {
                Log.e("$TAG#fetchNetworth", resource.error?.localizedDescription)
            }
            completion.invoke(resource)
        }
    }

    /**
     * Get the configuration for the navigation hierarchy for creation of
     * manual assets and liabilities. This allows the whitelabel to drive the
     * UX to only create an asset or liability which conforms to Backend rules.
     *
     * @param completion Completion handler with optional error if assets & liabilities json parsing fails or if it succeeds
     */
    fun fetchAssetsLiabilitiesConfig(completion: OnFrolloSDKCompletionListener<Resource<AssetsLiabilitiesResponse>>) {
        try {
            val jsonString = readStringFromJson(FrolloSDK.context, R.raw.assets_liabilities)
            val parsedObject = Gson().fromJson<AssetsLiabilitiesResponse>(jsonString)
            completion.invoke(Resource.success(parsedObject))
        } catch (e: Exception) {
            completion.invoke(Resource.error(FrolloSDKError(e.message)))
        }
    }
}
