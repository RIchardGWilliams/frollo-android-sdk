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

package us.frollo.frollosdk.model.coredata.contacts

import com.google.gson.annotations.SerializedName

/** Data representation of a International contact's bank details */
data class BankDetails(

    /** The name of the account holder */
    @SerializedName("country") var country: String,

    /** The accountNumber of the contact */
    @SerializedName("account_number") var accountNumber: String,

    /** The bankAddress of the of the contact (Optional) */
    @SerializedName("bank_address") var bankAddress: BankAddress?,

    /** The bic of the of the contact (Optional) */
    @SerializedName("bic") var bic: String?,

    /** The fedWireNumber of the of the contact (Optional) */
    @SerializedName("fed_wire_number") var fedWireNumber: String?,

    /** The sortCode of the of the contact (Optional) */
    @SerializedName("sort_code") var sortCode: String?,

    /** The chipNumber of the of the contact (Optional) */
    @SerializedName("chip_number") var chipNumber: String?,

    /** The routingNumber of the of the contact (Optional) */
    @SerializedName("routing_number") var routingNumber: String?,

    /** The legalEntityIdentifier of the of the contact (Optional) */
    @SerializedName("legal_entity_identifier") var legalEntityIdentifier: String?
)
