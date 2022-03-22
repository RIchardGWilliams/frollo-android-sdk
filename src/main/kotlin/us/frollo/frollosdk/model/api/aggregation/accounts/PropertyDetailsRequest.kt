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

package us.frollo.frollosdk.model.api.aggregation.accounts

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyPurpose
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyZoning

/** Property Details */
data class PropertyDetailsRequest(

    /** Property type */
    @SerializedName("type") var type: PropertyType,

    /** Property zoning */
    @SerializedName("zoning") var zoning: PropertyZoning,

    /** Primary purpose, eg. owner occupied */
    @SerializedName("purpose") var purpose: PropertyPurpose,

    /** Indicates if this is principal residence */
    @SerializedName("ppor") var principalResidence: Boolean,

    /** ID of address record from Frollo Addresses API */
    @SerializedName("address_id") var addressId: Long
)
