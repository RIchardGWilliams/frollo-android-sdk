package us.frollo.frollosdk.model.coredata.appconfiguration

import com.google.gson.annotations.SerializedName

data class Links(

    @SerializedName("key") val key: String,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
)
