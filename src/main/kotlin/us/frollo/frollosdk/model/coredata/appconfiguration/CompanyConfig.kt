package us.frollo.frollosdk.model.coredata.appconfiguration

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "company_config"
)

/** Represents the company details */
data class CompanyConfig(

    /** Company display name */
    @ColumnInfo(name = "display_name") @SerializedName("display_name") val displayName: String,

    /** Company legal name */
    @ColumnInfo(name = "legal_name") @SerializedName("legal_name") val legalName: String,

    /** Company ABN */
    @ColumnInfo(name = "abn") @SerializedName("abn") val abn: String?,

    /** Company ACN */
    @ColumnInfo(name = "acn") @SerializedName("acn") val acn: String?,

    /** Company Phone */
    @ColumnInfo(name = "phone") @SerializedName("phone") val phone: String?,

    /** Company Address */
    @ColumnInfo(name = "address") @SerializedName("address") val address: String?,

    /** Contact Email */
    @ColumnInfo(name = "support_email") @SerializedName("support_email") val supportEmail: String?,

    /** Contact Phone */
    @ColumnInfo(name = "support_phone") @SerializedName("support_phone") val supportPhone: String?
) {

    /** Unique ID of the company config */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "company_config_id") var companyConfigId: Long = 0
}
