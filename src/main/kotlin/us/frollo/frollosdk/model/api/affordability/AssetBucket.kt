package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountSubType

/** Asset Bucket(grouping) container of related manual assets */
data class AssetBucket(

    /** Type of the Asset Bucket */
    @SerializedName("type") val type: AssetBucketType,

    /** URL for fetching a remote image to show */
    @SerializedName("display_image_url") val displayImageUrl: String?,

    /** The groupings of property zones */
    @SerializedName("zoning") val zoning: List<PropertyZoningHierachy>?,

    /** List of allowed account subtypes */
    @SerializedName("account_types") val accountSubTypes: List<AccountSubType>?

)
