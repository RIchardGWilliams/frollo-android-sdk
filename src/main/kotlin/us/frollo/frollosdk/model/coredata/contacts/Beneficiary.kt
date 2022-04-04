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

/** Data representation of a International contact's beneficiary details */
data class Beneficiary(

    /** The name of the International contact (Optional) */
    @SerializedName("name") var name: String?,

    /** The country of the International contact */
    @SerializedName("country") var country: String,

    /** The message from International contact (Optional) */
    @SerializedName("message") var message: String?
)
