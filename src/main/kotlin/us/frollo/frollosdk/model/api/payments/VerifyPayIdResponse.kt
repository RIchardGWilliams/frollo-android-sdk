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

package us.frollo.frollosdk.model.api.payments

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.contacts.PayIDType
import us.frollo.frollosdk.model.coredata.payments.NPPServiceIdType

/**
 * VerifyPayIdResponse
 *
 * Represents the response of verify payID
 */
data class VerifyPayIdResponse(

    /** Value for the corresponding payIdType */
    @SerializedName("payid") val payId: String,

    /** The creditor PayID identifier type */
    @SerializedName("type") val type: PayIDType,

    /** Name of the registered PayID; shown to external parties when they attempt to make a Payment */
    @SerializedName("payid_name") val name: String,

    /** The service types available on this payment (Optional) */
    @SerializedName("service_types") val serviceTypes: List<NPPServiceIdType>?
)
