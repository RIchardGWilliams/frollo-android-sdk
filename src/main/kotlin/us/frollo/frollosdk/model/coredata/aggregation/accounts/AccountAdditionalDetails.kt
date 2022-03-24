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
import com.google.gson.annotations.SerializedName

/** Additional information to the account */
data class AccountAdditionalDetails(

    /** Additional description to the account (Optional) */
    @ColumnInfo(name = "description") @SerializedName("description") var description: String? = null,

    /** External image url (Optional) */
    @ColumnInfo(name = "image_url") @SerializedName("image_url") var imageUrl: String? = null,

    /** Additional information to property account (Optional) */
    @Embedded(prefix = "property_") @SerializedName("property") var propertyDetails: PropertyDetails? = null,

    /** Additional information to vehicle account (Optional) */
    @Embedded(prefix = "vehicle_") @SerializedName("vehicle") var vehicleDetails: VehicleDetails? = null
)
