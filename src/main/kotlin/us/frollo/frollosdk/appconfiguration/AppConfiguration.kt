package us.frollo.frollosdk.appconfiguration

import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.AppConfigurationAPI

/**
 * Provides Configuration of the app
 */
class AppConfiguration(network: NetworkService) {

    companion object {
        private const val TAG = "AppConfiguration"
    }

    private val appConfigAPI: AppConfigurationAPI = network.createExternalNoAuth(AppConfigurationAPI::class.java)

    /**
     * Fetch app configuration from the host and update the cache
     *
     * @param key Mandatory key for config lookup. Allows for different TYPES of config
     * @param completion completion Completion handler with option error if something occurs (optional)
     */
    fun refreshAppConfig(key: String, completion: OnFrolloSDKCompletionListener<Result>? = null) {
        appConfigAPI.fetchAppConfiguration(key).enqueue { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    completion?.invoke(Result.success())
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#fetchAppConfig", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
            }
        }
    }
}
