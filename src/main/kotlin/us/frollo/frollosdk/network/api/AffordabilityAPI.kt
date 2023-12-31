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

package us.frollo.frollosdk.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import us.frollo.frollosdk.model.api.affordability.ExportType
import us.frollo.frollosdk.model.api.affordability.FinancialPassportResponse
import us.frollo.frollosdk.model.api.affordability.NetworthResponse

internal interface AffordabilityAPI {
    companion object {
        const val URL_FINANCIAL_PASSPORT = "affordability/financialpassport"
        const val URL_FINANCIAL_PASSPORT_EXPORT = "affordability/financialpassport/export"
        const val URL_NETWORTH = "affordability/networth"
    }

    @GET(URL_FINANCIAL_PASSPORT)
    fun getFinancialPassport(@QueryMap queryParams: Map<String, String>): Call<FinancialPassportResponse>

    @GET(URL_FINANCIAL_PASSPORT_EXPORT)
    fun exportFinancialPassport(@Query("type") format: ExportType): Call<ResponseBody>

    @GET(URL_NETWORTH)
    fun fetchNetworth(@QueryMap queryParams: Map<String, String>): Call<NetworthResponse>
}
