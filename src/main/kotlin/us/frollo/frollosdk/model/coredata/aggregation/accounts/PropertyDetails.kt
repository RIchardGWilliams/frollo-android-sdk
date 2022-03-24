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

package us.frollo.frollosdk.model.coredata.aggregation.accounts

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.api.aggregation.accounts.AccountCreateUpdateRequest
import us.frollo.frollosdk.model.api.aggregation.accounts.AccountResponse
import us.frollo.frollosdk.model.coredata.user.UserAddress

/** Property Details */
data class PropertyDetails(

    /** Property type */
    @ColumnInfo(name = "type") @SerializedName("type") var type: PropertyType?,

    /** Property zoning */
    @ColumnInfo(name = "zoning") @SerializedName("zoning") var zoning: PropertyZoning?,

    /** Primary purpose, eg. owner occupied */
    @ColumnInfo(name = "purpose") @SerializedName("purpose") var purpose: PropertyPurpose?,

    /** Indicates if this is principal residence (Optional) */
    @ColumnInfo(name = "ppor") @SerializedName("ppor") var principalResidence: Boolean?,

    /**
     * Address record from Frollo Addresses API
     *
     * Note: This field is applicable only for the [AccountResponse] & [Account] Data Model
     */
    @Embedded(prefix = "address_") @SerializedName("address") val address: UserAddress?,

    /**
     * ID of address record from Frollo Addresses API (Optional)
     *
     * Note: This field is applicable only for [AccountCreateUpdateRequest]
     */
    @Ignore // Room ignores this field as we don't want to store this in DB so that we can reuse this same model for request & response
    @SerializedName("address_id") var requestAddressId: Long?
) {
    // This secondary constructor is needed to be able to use @Ignore parameters in primary constructor
    constructor(type: PropertyType, zoning: PropertyZoning, purpose: PropertyPurpose, principalResidence: Boolean?, address: UserAddress?) :
        this(type, zoning, purpose, principalResidence, address, null)
}
