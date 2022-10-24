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
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus

internal data class DisclosureConsentResponse(

    /** The ID of the consent */
    @SerializedName("id") val consentId: Long,

    /** The Status of disclosure consent */
    @SerializedName("status") val status: ConsentStatus,

    /** The Linked Collection Consent IDs (Optional) */
    @SerializedName("consent_ids") val linkedConsentIds: List<Long>?,

    /** The Permissions given for the Disclosure Consent - same as Collection permissions (Optional) */
    @SerializedName("permissions") val permissionIds: List<String>?,

    /** The disclosure duration in seconds of how long to disclose data for (Optional) */
    @SerializedName("disclosure_duration") val disclosureDuration: Long?,

    /** Start date of the sharing window. This date is the date when the consent officially starts on the Data Holder's end. (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("sharing_started_at") val sharingStartedAt: String?,

    /** Stopped sharing at date. The date the consent expired or was withdrawn. (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @SerializedName("sharing_stopped_at") val sharingStoppedAt: String?,

    /**  External party (Optional) */
    @SerializedName("external_party") val externalParty: ExternalPartyResponse?

)
