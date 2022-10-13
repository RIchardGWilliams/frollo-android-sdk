package us.frollo.frollosdk.model.api.appconfiguration

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig

data class AppConfigurationResponse(

    @SerializedName("company") val company: CompanyConfig?,
    @SerializedName("links") val links: List<LinkConfig>?,
    @SerializedName("features") val features: List<FeatureConfig>?
)
