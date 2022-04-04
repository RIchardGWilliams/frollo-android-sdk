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
import us.frollo.frollosdk.model.coredata.aggregation.providers.AggregatorType
import us.frollo.frollosdk.model.coredata.contacts.PaymentDetails
import us.frollo.frollosdk.model.coredata.contacts.PaymentMethod

internal data class ContactResponse(
    @SerializedName("id") val contactId: Long,
    @SerializedName("created_date") val createdDate: String,
    @SerializedName("modified_date") val modifiedDate: String,
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("related_provider_account_ids") val relatedProviderAccountIds: List<Long>?,
    @SerializedName("name") val name: String,
    @SerializedName("nick_name") val nickName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("aggregator") val aggregatorType: AggregatorType,
    @SerializedName("consent_id") val consentId: Long?,
    @SerializedName("editable") val editable: Boolean,
    @SerializedName("payment_method") val paymentMethod: PaymentMethod,

    // DO NOT add @SerializedName("payment_details") to this field as it cannot be directly
    // de-serialized as PaymentDetails is abstract and hence we are using ContactResponseDeserializer to initialize this field
    var paymentDetails: PaymentDetails? // NOTE: Any update to paymentDetails field ensure you update ContactResponseDeserializer
)
