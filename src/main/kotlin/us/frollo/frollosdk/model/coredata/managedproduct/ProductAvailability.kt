package us.frollo.frollosdk.model.coredata.managedproduct

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/**  */
enum class ProductAvailability {

    /** Product availability during onboarding is required */
    @SerializedName("onboarding_required") ONBOARDING_REQUIRED,

    /** Product availability during onboarding is required */
    @SerializedName("onboarding_optional") ONBOARDING_OPTIONAL,

    /** Product availability after onboarding */
    @SerializedName("post_onboarding") POST_ONBOARDING;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
