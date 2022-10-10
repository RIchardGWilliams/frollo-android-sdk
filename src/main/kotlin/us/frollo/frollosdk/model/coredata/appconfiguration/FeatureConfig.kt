package us.frollo.frollosdk.model.coredata.appconfiguration

import com.google.gson.annotations.SerializedName

/** Represents the features for the app */
data class FeatureConfig(

    /** Feature key for app to lookup by */
    @SerializedName("key") val key: String,

    /** Name of feature */
    @SerializedName("name") val name: String,

    /** Enabled/disabled state of the feature */
    @SerializedName("enabled") val enabled: Boolean
)
