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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap
import us.frollo.frollosdk.model.api.contacts.ContactCreateUpdateRequest
import us.frollo.frollosdk.model.api.contacts.ContactInternationalCreateUpdateRequest
import us.frollo.frollosdk.model.api.contacts.ContactResponse
import us.frollo.frollosdk.model.api.shared.PaginatedResponse

internal interface ContactsAPI {

    companion object {
        const val URL_CONTACTS = "contacts"
        const val URL_CONTACT = "contacts/{contact_id}"
    }

    @GET(URL_CONTACTS)
    fun fetchContacts(@QueryMap options: Map<String, String>): Call<PaginatedResponse<ContactResponse>>

    @GET(URL_CONTACT)
    fun fetchContact(@Path("contact_id") contactId: Long): Call<ContactResponse>

    @POST(URL_CONTACTS)
    fun createContact(@Body request: ContactCreateUpdateRequest): Call<ContactResponse>

    @POST(URL_CONTACTS)
    fun createInternationalContact(@Body request: ContactInternationalCreateUpdateRequest): Call<ContactResponse>

    @PUT(URL_CONTACT)
    fun updateContact(@Path("contact_id") contactId: Long, @Body request: ContactCreateUpdateRequest): Call<ContactResponse>

    @PUT(URL_CONTACT)
    fun updateInternationalContact(@Path("contact_id") contactId: Long, @Body request: ContactInternationalCreateUpdateRequest): Call<ContactResponse>

    @DELETE(URL_CONTACT)
    fun deleteContact(@Path("contact_id") contactId: Long): Call<Void>
}
