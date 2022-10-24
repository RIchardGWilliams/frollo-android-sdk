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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap
import us.frollo.frollosdk.model.api.cdr.CDRConfigurationResponse
import us.frollo.frollosdk.model.api.cdr.CDRDisclosureConsentRequest
import us.frollo.frollosdk.model.api.cdr.CDRDisclosureConsentsResponse
import us.frollo.frollosdk.model.api.cdr.ConsentCreateRequest
import us.frollo.frollosdk.model.api.cdr.ConsentResponse
import us.frollo.frollosdk.model.api.cdr.ConsentUpdateRequest
import us.frollo.frollosdk.model.api.cdr.ExternalPartyResponse
import us.frollo.frollosdk.model.api.shared.PaginatedResponse
import us.frollo.frollosdk.model.coredata.aggregation.providers.CDRProduct

internal interface CdrAPI {
    companion object {
        const val URL_CDR_CONFIG = "config/cdr/{external_id}"
        const val URL_CDR_CONSENTS = "cdr/consents"
        const val URL_CDR_CONSENT = "cdr/consents/{consent_id}"
        const val URL_CDR_PRODUCTS = "cdr/products"
        const val URL_CDR_PRODUCT = "cdr/products/{product_id}"
        const val URL_EXTERNAL_PARTIES = "cdr/parties/external"
        const val URL_EXTERNAL_PARTY = "cdr/parties/external/{external_party_id}"
        const val URL_CDR_DISCLOSURE_CONSENTS = "cdr/disclosure"
    }

    @GET(URL_CDR_CONFIG)
    fun fetchCDRConfig(@Path("external_id") externalId: String): Call<CDRConfigurationResponse>

    @GET(URL_CDR_PRODUCTS)
    fun fetchProducts(@QueryMap queryParams: Map<String, String>): Call<List<CDRProduct>>

    @GET(URL_CDR_PRODUCT)
    fun fetchProduct(@Path("product_id") productId: Long): Call<CDRProduct>

    @GET(URL_CDR_CONSENTS)
    fun fetchConsents(@QueryMap options: Map<String, String>): Call<PaginatedResponse<ConsentResponse>>

    @GET(URL_CDR_CONSENT)
    fun fetchConsent(@Path("consent_id") consentId: Long): Call<ConsentResponse>

    @POST(URL_CDR_CONSENTS)
    fun submitConsent(@Body request: ConsentCreateRequest): Call<ConsentResponse>

    @PUT(URL_CDR_CONSENT)
    fun updateConsent(@Path("consent_id") consentId: Long, @Body request: ConsentUpdateRequest): Call<ConsentResponse>

    @GET(URL_EXTERNAL_PARTIES)
    fun fetchExternalParties(@QueryMap queryParams: Map<String, String>): Call<PaginatedResponse<ExternalPartyResponse>>

    @GET(URL_EXTERNAL_PARTY)
    fun fetchExternalParty(@Path("external_party_id") partyId: Long): Call<ExternalPartyResponse>

    @GET(URL_CDR_DISCLOSURE_CONSENTS)
    fun fetchDisclosureConsents(@Body request: CDRDisclosureConsentRequest): Call<PaginatedResponse<CDRDisclosureConsentsResponse>>
}
