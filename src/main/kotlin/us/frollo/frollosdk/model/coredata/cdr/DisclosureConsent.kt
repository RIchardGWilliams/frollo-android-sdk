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

package us.frollo.frollosdk.model.coredata.cdr

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "disclosure_consent",
    indices = [
        Index("consent_id")
    ]
)

/** Represents key information of Disclosure consent */
data class DisclosureConsent(

    /** The ID of the consent */
    @PrimaryKey @ColumnInfo(name = "consent_id") val consentId: Long,

    /** The Status of disclosure consent */
    @ColumnInfo(name = "status") val status: ConsentStatus,

    /** The Linked Collection Consent IDs (Optional) */
    @ColumnInfo(name = "consent_ids") val linkedConsentIds: List<Long>?,

    /** The Permissions given for the Disclosure Consent - same as Collection permissions (Optional) */
    @ColumnInfo(name = "permissions") val permissions: List<String>?,

    /** The disclosure duration in seconds of how long to disclose data for (Optional) */
    @ColumnInfo(name = "disclosure_duration") val disclosureDuration: Long?,

    /** Start date of the sharing window. This date is the date when the consent officially starts on the Data Holder's end. (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "sharing_started_at") val sharingStartedAt: String?,

    /** Stopped sharing at date. The date the consent expired or was withdrawn. (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "sharing_stopped_at") val sharingStoppedAt: String?,

    /**
     * The date the consent will expire if not withdrawn sooner (Optional)
     *
     * Date format for this field is ISO8601
     * example 2011-12-03T10:15:30+01:00
     */
    @ColumnInfo(name = "sharing_expires_at") val sharingExpiresAt: String?,

    /**  External party (Optional) */
    @ColumnInfo(name = "external_party") val externalParty: ExternalParty?
)
