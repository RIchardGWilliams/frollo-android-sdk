package us.frollo.frollosdk.model.coredata.cdr

import com.google.gson.annotations.SerializedName

/** Represents a CDR Policy */
data class CDRPolicy(

    /** Name of the policy */
    @SerializedName("name") val name: String,

    /** URL of policy */
    @SerializedName("url") val url: String
)
