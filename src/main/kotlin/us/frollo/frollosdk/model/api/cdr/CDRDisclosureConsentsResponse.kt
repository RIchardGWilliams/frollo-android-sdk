package us.frollo.frollosdk.model.api.cdr

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.cdr.CDRDisclosureConsentPermission
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus

internal data class CDRDisclosureConsentsResponse(

    /** The ID of the consent */
    @SerializedName("id") val consentId: Long?,

    /** The Status of disclosure consent */
    @SerializedName("status") val status: ConsentStatus?,

    /** The Linked Collection Consent IDs (Optional) */
    @SerializedName("consent_ids") val consent_ids: List<Long>?,

    /** The Permissions given for the Disclosure Consent - same as Collection permissions (Optional) */
    @SerializedName("permissions") val permissions: List<CDRDisclosureConsentPermission>,

    /** The disclosure duration in seconds of how long to disclose data for (Optional) */
    @SerializedName("disclosure_duration") val disclosureDuration: Int?,

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
