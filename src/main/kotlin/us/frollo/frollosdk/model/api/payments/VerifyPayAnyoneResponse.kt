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
import us.frollo.frollosdk.model.coredata.payments.BSBAddress
import us.frollo.frollosdk.model.coredata.payments.NPPServiceIdType

/**
 * VerifyPayAnyoneResponse
 *
 * Represents the response of verify pay anyone
 */
data class VerifyPayAnyoneResponse(

    /** True if the lookup is valid, false otherwise */
    @SerializedName("valid") val valid: Boolean,

    /** BSB number if valid is true null otherwise (Optional) */
    @SerializedName("bsb") val bsb: String?,

    /** Institution Mnemonic (Optional) */
    @SerializedName("mnemonic") val institutionMnemonic: String?,

    /** Name of the bank (Optional) */
    @SerializedName("name") val name: String?,

    /** Address of the bank (Optional) */
    @SerializedName("address") val address: BSBAddress?,

    /** Payment Flags */
    @SerializedName("payments_flags") val paymentsFlags: String?,

    /** Indicates if NPP is allowed for Payment */
    @SerializedName("npp_allowed") val isNppAllowed: Boolean?,

    /** The service ids available on this payment (Optional) */
    @SerializedName("npp_service_ids") val nppServiceIds: List<NPPServiceIdType>?
)
