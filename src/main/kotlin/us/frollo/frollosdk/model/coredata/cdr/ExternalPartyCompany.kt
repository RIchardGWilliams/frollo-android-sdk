package us.frollo.frollosdk.model.coredata.cdr

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

/** Represents Company details of an External Party */
data class ExternalPartyCompany(

    /** Company display name */
    @ColumnInfo(name = "display_name") @SerializedName("display_name") val displayName: String,

    /** Company legal name */
    @ColumnInfo(name = "legal_name") @SerializedName("legal_name") val legalName: String
)
