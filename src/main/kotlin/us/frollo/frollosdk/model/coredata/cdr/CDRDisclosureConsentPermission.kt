package us.frollo.frollosdk.model.coredata.cdr

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.extensions.serializedName

/** Indicates permissions given for disclosure consent */
@Keep
enum class CDRDisclosureConsentPermission {

    /** Account details */
    @SerializedName("account_details") ACCOUNT_DETAILS,

    /** Transaction details */
    @SerializedName("transaction_details") TRANSACTION_DETAILS,

    /** Customer details */
    @SerializedName("customer_details") CUSTOMER_DETAILS,

    /** Payee details */
    @SerializedName("payee_details") PAYEE_DETAILS,

    /** Contact details */
    @SerializedName("contact_details") CONTACT_DETAILS;

    /** Enum to serialized string */
    // This override MUST be used for this enum to work with Retrofit @Path or @Query parameters
    override fun toString(): String =
        // Try to get the annotation value if available instead of using plain .toString()
        // Fallback to super.toString() in case annotation is not present/available
        serializedName() ?: super.toString()
}
