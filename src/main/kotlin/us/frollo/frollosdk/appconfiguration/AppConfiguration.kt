package us.frollo.frollosdk.appconfiguration

import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.model.api.appconfiguration.AppConfigurationResponse
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.AppConfigurationAPI

/**
 * Provides Configuration of the app
 */
class AppConfiguration(network: NetworkService) {

    companion object {
        private const val TAG = "AppConfig"
    }

    private val appConfigAPI: AppConfigurationAPI = network.create(AppConfigurationAPI::class.java)

    /**
     * Fetch app configuration
     *
     * @param key Mandatory key for config lookup. Allows for different TYPES of config
     * @param completion Completion handler with optional error if the request fails else [AppConfigurationResponse] if success
     */
    fun fetchAppConfig(key: String, completion: OnFrolloSDKCompletionListener<Resource<AppConfigurationResponse>>) {
        appConfigAPI.fetchAppConfiguration(key).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    completion.invoke(resource)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#fetchAppConfig", resource.error?.localizedDescription)
                    completion.invoke(resource)
                }
            }
        }
    }
}
