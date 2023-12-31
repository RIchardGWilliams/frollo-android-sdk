package us.frollo.frollosdk.model.api.affordability

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.IAdapterModel

/** Asset Bucket Types */
@Keep
enum class AssetBucketType : IAdapterModel {

    /** ACCOUNT_FUNDS */
    @SerializedName("accounts_funds") ACCOUNT_FUNDS,

    /** MOTOR_VEHICLE */
    @SerializedName("motor_vehicle") MOTOR_VEHICLE,

    /** OTHER_ASSETS */
    @SerializedName("other_assets") OTHER_ASSETS,

    /** PROPERTY */
    @SerializedName("property") PROPERTY,
}
