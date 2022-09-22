package us.frollo.frollosdk.model.coredata.cdr

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.IAdapterModel

/** Represents a CDR Party */
data class CDRParty(

    /** The Unique ID of the CDR Party */
    @SerializedName("id") val partyId: Long,

    /** The name of the CDR Party */
    @SerializedName("name") val name: String,

    /** The summary of the CDR Party */
    @SerializedName("summary") val summary: String,

    /** The description of the CDR Party */
    @SerializedName("description") val description: String,

    /** The description of the CDR Party */
    @SerializedName("image_url") val imageUrl: String,

    /** Type of the CDR Party */
    @SerializedName("type") val type: CDRPartyType,

    /** ADR or Affiliate ID if applicable (Optional) */
    @SerializedName("adr_id") val adrId: String?,

    /** ABN number if applicable (Optional) */
    @SerializedName("registration_number") val registrationNumber: String?,

    /** CDR or privacy policy */
    @SerializedName("policy") val policy: CDRPolicy

) : IAdapterModel
