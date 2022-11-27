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
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "external_party",
    indices = [
        Index("party_id")
    ]
)

/** Data representation ExternalParty */
/*
   NOTE: We have added SerializedName annotation here as well
   because it is needed by DisclosureConsent.kt and
   Converters.kt (stringToExternalParty and stringFromExternalParty)
*/
data class ExternalParty(

    /** Unique ID of the External Party */
    @PrimaryKey @ColumnInfo(name = "party_id") @SerializedName("id") val partyId: Long,

    /** External Party external reference ID (Optional) */
    @ColumnInfo(name = "external_id") @SerializedName("external_id") val externalId: String?,

    /** Unique key generated for the External Party */
    @ColumnInfo(name = "key") @SerializedName("key") val key: String,

    /** External Party name */
    @ColumnInfo(name = "name") @SerializedName("name") val name: String,

    /** External Party display name (Optional) */
    @ColumnInfo(name = "display_name") @SerializedName("support_phone") val displayName: String?,

    /** Company details */
    @Embedded(prefix = "company_") @SerializedName("company") val company: ExternalPartyCompany?,

    /** Contact could be an email, phone number or a website */
    @ColumnInfo(name = "contact") @SerializedName("contact") val contact: String,

    /** External Party description (Optional) */
    @ColumnInfo(name = "description") @SerializedName("description") val description: String?,

    /** Status of External Party */
    @ColumnInfo(name = "status") @SerializedName("status") val status: ExternalPartyStatus,

    /** External Party image URL (Optional) */
    @ColumnInfo(name = "image_url") @SerializedName("image_url") val imageUrl: String?,

    /** External Party small image URL (Optional) */
    @ColumnInfo(name = "small_image_url") @SerializedName("small_image_url") val smallImageUrl: String?,

    /** External Party privacy policy URL */
    @ColumnInfo(name = "privacy_url") @SerializedName("privacy_url") val privacyUrl: String,

    /** Type of External Party */
    @ColumnInfo(name = "type") @SerializedName("type") val type: ExternalPartyType,

    /** Type of Trusted Advisor (Optional) */
    @ColumnInfo(name = "ta_type") @SerializedName("ta_type") val trustedAdvisorType: TrustedAdvisorType?,

    /** External Party summary (Optional) */
    @ColumnInfo(name = "summary") @SerializedName("summary") val summary: String?,

    /** The sharing durations for the External Party (Optional) */
    @ColumnInfo(name = "sharing_durations") @SerializedName("sharing_durations") val sharingDurations: List<SharingDuration>?,

    /** Permissions for the CDR Configuration (Optional) */
    @ColumnInfo(name = "permissions") @SerializedName("permissions") val permissions: List<CDRPermission>?,

    /** External Party Phone (Optional) */
    @ColumnInfo(name = "phone") @SerializedName("phone") val phone: String?,

    /** External party Address (Optional) */
    @ColumnInfo(name = "address") @SerializedName("address") val address: String?,

    /** External party support email (Optional) */
    @ColumnInfo(name = "support_email") @SerializedName("support_email") val supportEmail: String?,

    /** External party support phone (Optional) */
    @ColumnInfo(name = "support_phone") @SerializedName("support_phone") val supportPhone: String?,

    /** External party website url (Optional) */
    @ColumnInfo(name = "website_url") @SerializedName("website_url") val websiteUrl: String?
)
