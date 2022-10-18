package us.frollo.frollosdk.model.coredata.appconfiguration

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Declaring the ColumnInfo allows for the renaming of variables without
// implementing a database migration, as the column name would not change.

@Entity(
    tableName = "feature_config",
    indices = [
        Index("key")
    ]
)

/** Represents the features for the app */
data class FeatureConfig(

    /** Feature key for app to lookup by */
    @PrimaryKey @ColumnInfo(name = "key") @SerializedName("key") val key: String,

    /** Name of feature */
    @ColumnInfo(name = "name") @SerializedName("name") val name: String,

    /** Enabled/disabled state of the feature */
    @ColumnInfo(name = "enabled") @SerializedName("enabled") val enabled: Boolean
)
