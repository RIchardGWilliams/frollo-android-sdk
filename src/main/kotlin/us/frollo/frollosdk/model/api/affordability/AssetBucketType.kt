package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName

/** Asset Bucket Types */
enum class AssetBucketType {

    /** ACCOUNT_FUNDS */
    @SerializedName("accounts_funds") ACCOUNT_FUNDS,

    /** MOTOR_VEHICLE */
    @SerializedName("motor_vehicle") MOTOR_VEHICLE,

    /** OTHER_ASSETS */
    @SerializedName("other_assets") OTHER_ASSETS,

    /** PROPERTY */
    @SerializedName("property") PROPERTY,
}
