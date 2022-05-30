package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName

/**
 * Data representation of hierarchy of allowed Assets / Liabilities
 * critical for navigation by whitelabel for creating a manual asset / liability Account object.
 */
data class AssetsLiabilitiesResponse(

    // Assets are an array of asset bucket, one for each of the major categories e.g. property, vehicle
    @SerializedName("assets") val assets: List<AssetBucket>,

    // list of account (sub) types that are allowed to be used for manual liability
    @SerializedName("liabilities") val liabilities: LiabilitiesBucket
)
