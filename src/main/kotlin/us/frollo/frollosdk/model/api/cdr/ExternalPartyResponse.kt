/*
 * Copyright 2020 Frollo
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

package us.frollo.frollosdk.model.api.cdr

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.cdr.CDRPermission
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyCompany
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyStatus
import us.frollo.frollosdk.model.coredata.cdr.ExternalPartyType
import us.frollo.frollosdk.model.coredata.cdr.SharingDuration
import us.frollo.frollosdk.model.coredata.cdr.TrustedAdvisorType

internal data class ExternalPartyResponse(
    @SerializedName("id") val partyId: Long,
    @SerializedName("external_id") val externalId: String?,
    @SerializedName("name") val name: String,
    @SerializedName("company") val company: ExternalPartyCompany?,
    @SerializedName("contact") val contact: String,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: ExternalPartyStatus,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("small_image_url") val smallImageUrl: String?,
    @SerializedName("privacy_url") val privacyUrl: String,
    @SerializedName("type") val type: ExternalPartyType,
    @SerializedName("ta_type") val trustedAdvisorType: TrustedAdvisorType?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("sharing_durations") val sharingDurations: List<SharingDuration>?,
    @SerializedName("permissions") val permissions: List<CDRPermission>?
)
