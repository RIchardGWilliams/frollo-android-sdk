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

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url
import us.frollo.frollosdk.model.api.servicestatus.ServiceOutageResponse
import us.frollo.frollosdk.model.api.servicestatus.ServiceStatusResponse
import us.frollo.frollosdk.network.NetworkHelper

internal interface ServiceStatusAPI {

    @GET
    fun fetchServiceStatus(@Url url: String, @Header(NetworkHelper.HEADER_HOST) host: String): Call<ServiceStatusResponse>

    @GET
    fun fetchServiceOutages(@Url url: String, @Header(NetworkHelper.HEADER_HOST) host: String): Call<List<ServiceOutageResponse>>
}
