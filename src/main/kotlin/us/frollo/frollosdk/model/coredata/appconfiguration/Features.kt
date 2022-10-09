package us.frollo.frollosdk.model.coredata.appconfiguration

import com.google.gson.annotations.SerializedName

data class Features(

    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String,
    @SerializedName("enabled") val enabled: Boolean,
)
