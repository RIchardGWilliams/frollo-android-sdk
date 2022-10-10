package us.frollo.frollosdk.model.coredata.appconfiguration

import com.google.gson.annotations.SerializedName

/** Represents the links for the app */
data class LinkConfig(

    /** Link key */
    @SerializedName("key") val key: String,

    /** Name of the link */
    @SerializedName("name") val name: String,

    /** URL of the link */
    @SerializedName("url") val url: String
)
