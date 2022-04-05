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

package us.frollo.frollosdk.model.api.contacts

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.contacts.PaymentMethod

internal data class ContactInternationalCreateUpdateRequest(
    @SerializedName("nick_name") val nickName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("payment_method") val paymentMethod: PaymentMethod = PaymentMethod.INTERNATIONAL,
    @SerializedName("payment_details") val paymentDetails: InternationalPaymentDetails
) {
    internal data class InternationalPaymentDetails(
        @SerializedName("name") val name: String? = null,
        @SerializedName("country") val country: String,
        @SerializedName("message") val message: String? = null,
        @SerializedName("bank_country") val bankCountry: String,
        @SerializedName("account_number") val accountNumber: String,
        @SerializedName("bank_address") val bankAddress: String? = null,
        @SerializedName("bic") val bic: String? = null,
        @SerializedName("fed_wire_number") val fedWireNumber: String? = null,
        @SerializedName("sort_code") val sortCode: String? = null,
        @SerializedName("chip_number") val chipNumber: String? = null,
        @SerializedName("routing_number") val routingNumber: String? = null,
        @SerializedName("legal_entity_identifier") val legalEntityIdentifier: String? = null
    )
}
