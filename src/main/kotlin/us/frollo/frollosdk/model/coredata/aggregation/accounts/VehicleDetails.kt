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

import com.google.gson.annotations.SerializedName

/** Vehicle Details */
data class VehicleDetails(

    /** Vehicle type */
    @SerializedName("type") var type: VehicleType,

    /** Manufacture Year. Date Format YYYY (Optional) */
    @SerializedName("manufacture_year") var manufactureYear: String? = null,

    /** Vehicle maker (Optional) */
    @SerializedName("make") var make: String? = null,

    /** Vehicle model (Optional) */
    @SerializedName("model") var model: Boolean? = null
)
