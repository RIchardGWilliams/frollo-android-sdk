package us.frollo.frollosdk.model.api.affordability

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyType
import us.frollo.frollosdk.model.coredata.aggregation.accounts.PropertyZoning

/** Contains information about which property types belong to a specific Property zone */
data class PropertyZoningHierachy(

    /** PropertyTypes included in a zone */
    @SerializedName("types") val propertyTypes: List<PropertyType>,

    /** Property Zone value */
    @SerializedName("type") val zone: PropertyZoning,

    /** Display Image URL (Optional) */
    @SerializedName("display_image_url") val displayImageUrl: String?
)
