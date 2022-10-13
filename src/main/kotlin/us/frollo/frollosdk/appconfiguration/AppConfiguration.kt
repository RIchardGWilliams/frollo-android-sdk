package us.frollo.frollosdk.appconfiguration

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.base.doAsync
import us.frollo.frollosdk.base.uiThread
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.database.SDKDatabase
import us.frollo.frollosdk.extensions.enqueue
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.model.api.appconfiguration.AppConfigurationResponse
import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig
import us.frollo.frollosdk.network.NetworkService
import us.frollo.frollosdk.network.api.AppConfigurationAPI

/**
 * Provides Configuration of the app
 */
class AppConfiguration(network: NetworkService, internal val db: SDKDatabase) {

    companion object {
        private const val TAG = "AppConfiguration"
    }

    private val appConfigAPI: AppConfigurationAPI = network.createExternalNoAuth(AppConfigurationAPI::class.java)

    /**
     * Fetch company config from the host and update the cache
     */
    fun fetchCompanyConfig(): LiveData<Resource<CompanyConfig>> {
        return Transformations.map(db.companyConfig().load()) {
            Resource.success(it)
        }
    }

    /**
     * Fetch feature config from the host and update the cache
     */
    fun fetchFeatureConfig(): LiveData<Resource<List<FeatureConfig>>> {
        return Transformations.map(db.featureConfig().load()) {
            Resource.success(it)
        }
    }

    /**
     * Fetch link config from the host and update the cache
     */
    fun fetchLinkConfig(): LiveData<Resource<List<LinkConfig>>> {
        return Transformations.map(db.linkConfig().load()) {
            Resource.success(it)
        }
    }

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
                    handleAppConfigurationResponse(resource.data, completion)
                }
                Resource.Status.ERROR -> {
                    Log.e("$TAG#fetchAppConfig", resource.error?.localizedDescription)
                    completion?.invoke(Result.error(resource.error))
                }
            }
        }
    }

    private fun handleAppConfigurationResponse(
        response: AppConfigurationResponse?,
        completion: OnFrolloSDKCompletionListener<Result>?
    ) {
        response?.let {
            doAsync {
                response.company?.let {
                    db.companyConfig().clear()
                    db.companyConfig().insert(it)
                }
                response.features?.let {
                    db.featureConfig().insertAll(*it.toTypedArray())
                }
                response.links?.let {
                    db.linkConfig().insertAll(*it.toTypedArray())
                }
                uiThread { completion?.invoke(Result.success()) }
            }
        } ?: run { completion?.invoke(Result.success()) } // Explicitly invoke completion callback if response is null.
    }
}
