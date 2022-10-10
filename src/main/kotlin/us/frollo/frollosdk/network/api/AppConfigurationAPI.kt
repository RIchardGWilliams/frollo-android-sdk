package us.frollo.frollosdk.network.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import us.frollo.frollosdk.model.api.appconfiguration.AppConfigurationResponse

internal interface AppConfigurationAPI {

    companion object {
        const val URL_APP_CONFIGURATION = "config/app/{key}"
    }

    @GET(URL_APP_CONFIGURATION)
    fun fetchAppConfiguration(@Path("key") key: String): Call<AppConfigurationResponse>
}
