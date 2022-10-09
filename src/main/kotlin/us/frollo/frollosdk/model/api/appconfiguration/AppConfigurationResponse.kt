package us.frollo.frollosdk.model.api.appconfiguration

import com.google.gson.annotations.SerializedName
import us.frollo.frollosdk.model.coredata.appconfiguration.Company
import us.frollo.frollosdk.model.coredata.appconfiguration.Features
import us.frollo.frollosdk.model.coredata.appconfiguration.Links

data class AppConfigurationResponse(

    @SerializedName("company") val company: Company,
    @SerializedName("links") val links: List<Links>,
    @SerializedName("features") val features: List<Features>
)
