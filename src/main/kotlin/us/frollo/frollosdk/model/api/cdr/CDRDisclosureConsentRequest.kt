package us.frollo.frollosdk.model.api.cdr

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus

internal data class CDRDisclosureConsentRequest(

    /** After the specified cursor */
    @SerializedName("after") val after: Long,

    /** Before the specified cursor */
    @SerializedName("before") val before: Long,

    /** Filter by consent status */
    @SerializedName("status") val status: ConsentStatus?,
)
