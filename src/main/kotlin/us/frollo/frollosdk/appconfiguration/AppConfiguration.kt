package us.frollo.frollosdk.appconfiguration

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
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
     * Fetch company config from the cache
     */
    fun fetchCompanyConfig(): LiveData<CompanyConfig?> {
        return db.companyConfig().load()
    }

    /**
     * Advanced method to fetch company config by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches  company config from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of CompanyConfig which can be observed using an Observer for future changes as well.
     */
    fun fetchCompanyConfig(query: SimpleSQLiteQuery): LiveData<CompanyConfig?> =
        db.companyConfig().loadByQuery(query)

    /**
     * Fetch feature config from the cache
     */
    fun fetchFeatureConfig(): LiveData<List<FeatureConfig>> {
        return db.featureConfig().load()
    }

    /**
     * Advanced method to fetch features config by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches features config from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of List<FeatureConfig> which can be observed using an Observer for future changes as well.
     */
    fun fetchFeatureConfig(query: SimpleSQLiteQuery): LiveData<List<FeatureConfig>> =
        db.featureConfig().loadByQuery(query)

    /**
     * Fetch link config from the cache
     */
    fun fetchLinkConfig(): LiveData<List<LinkConfig>> {
        return db.linkConfig().load()
    }

    /**
     * Advanced method to fetch links config by SQL query from the cache
     *
     * @param query SimpleSQLiteQuery: Select query which fetches links config from the cache
     *
     * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
     *
     * @return LiveData object of List<LinkConfig> which can be observed using an Observer for future changes as well.
     */
    fun fetchLinkConfig(query: SimpleSQLiteQuery): LiveData<List<LinkConfig>> =
        db.linkConfig().loadByQuery(query)

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
                    Log.e("$TAG#refreshAppConfig", resource.error?.localizedDescription)
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

                    val apiKeys = it.map { featureconfig ->
                        featureconfig.key
                    }.toList()

                    val staleKeys = db.featureConfig().getStaleKeys(apiKeys.toTypedArray())

                    if (staleKeys.isNotEmpty()) {
                        db.featureConfig().deleteMany(staleKeys.toTypedArray())
                    }
                }

                response.links?.let {
                    db.linkConfig().insertAll(*it.toTypedArray())

                    val apiKeys = it.map { linkConfig ->
                        linkConfig.key
                    }.toList()

                    val staleKeys = db.linkConfig().getStaleKeys(apiKeys.toTypedArray())

                    if (staleKeys.isNotEmpty()) {
                        db.linkConfig().deleteMany(staleKeys.toTypedArray())
                    }
                }

                uiThread { completion?.invoke(Result.success()) }
            }
        } ?: run { completion?.invoke(Result.success()) } // Explicitly invoke completion callback if response is null.
    }
}
