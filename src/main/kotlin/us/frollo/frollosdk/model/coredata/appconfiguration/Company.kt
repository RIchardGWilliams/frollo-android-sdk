package us.frollo.frollosdk.model.coredata.appconfiguration

import com.google.gson.annotations.SerializedName

data class Company(

    @SerializedName("display_name") val displayName: String,
    @SerializedName("legal_name") val legalName: String,
    @SerializedName("abn") val abn: String?,
    @SerializedName("acn") val acn: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("support_email") val supportEmail: String?,
    @SerializedName("support_phone") val supportPhone: String?,
)
