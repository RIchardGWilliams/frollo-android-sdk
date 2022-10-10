package us.frollo.frollosdk.model.coredata.appconfiguration

import com.google.gson.annotations.SerializedName

/** Represents the company details */
data class CompanyConfig(

    /** Company display name */
    @SerializedName("display_name") val displayName: String,

    /** Company legal name */
    @SerializedName("legal_name") val legalName: String,

    /** Company ABN */
    @SerializedName("abn") val abn: String?,

    /** Company ACN */
    @SerializedName("acn") val acn: String?,

    /** Company Phone */
    @SerializedName("phone") val phone: String?,

    /** Company Address */
    @SerializedName("address") val address: String?,

    /** Contact Email */
    @SerializedName("support_email") val supportEmail: String?,

    /** Contact Phone */
    @SerializedName("support_phone") val supportPhone: String?
)
