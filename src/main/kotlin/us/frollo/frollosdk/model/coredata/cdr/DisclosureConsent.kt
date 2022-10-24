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

    /**  External party (Optional) */
    @ColumnInfo(name = "external_party") val externalParty: ExternalParty?
)
