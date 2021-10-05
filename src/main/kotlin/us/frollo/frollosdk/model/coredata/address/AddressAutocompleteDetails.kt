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

package us.frollo.frollosdk.model.coredata.address

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.IAdapterModel
import java.io.Serializable

/**
 * Data representation of Address Autocomplete Details
 */
data class AddressAutocompleteDetails(

    /** Address line 1. (Optional) */
    @SerializedName("line_1") val line1: String? = null,

    /** Address line 2. (Optional) */
    @SerializedName("line_2") val line2: String? = null,

    /** Address suburb name. (Optional) */
    @SerializedName("suburb") var suburb: String? = null,

    /** Address town name. (Optional) */
    @SerializedName("town") var town: String? = null,

    /** Address region. (Optional) */
    @SerializedName("region") var region: String? = null,

    /** Address state. (Optional) */
    @SerializedName("state") var state: String? = null,

    /** Address country. (Optional) */
    @SerializedName("country") var country: String? = null,

    /** Address postcode. (Optional) */
    @SerializedName("postal_code") var postcode: String? = null,

    /** Full address in formatted form. (Optional) */
    @SerializedName("long_form") var longForm: String? = null

) : IAdapterModel, Serializable
