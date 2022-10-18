package us.frollo.frollosdk.model.coredata.appconfiguration

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "link_config",
    indices = [
        Index("key")
    ]
)

/** Represents the links for the app */
data class LinkConfig(

    /** Link key */
    @PrimaryKey @ColumnInfo(name = "key") @SerializedName("key") val key: String,

    /** Name of the link */
    @ColumnInfo(name = "name") @SerializedName("name") val name: String,

    /** URL of the link */
    @ColumnInfo(name = "url") @SerializedName("url") val url: String

)
