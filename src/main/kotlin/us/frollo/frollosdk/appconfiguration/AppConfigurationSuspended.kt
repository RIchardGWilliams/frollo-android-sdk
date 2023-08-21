package us.frollo.frollosdk.appconfiguration

import androidx.sqlite.db.SimpleSQLiteQuery
import us.frollo.frollosdk.aggregation.Aggregation
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.logging.Log
import us.frollo.frollosdk.model.api.appconfiguration.AppConfigurationResponse
import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig
import us.frollo.frollosdk.network.makeApiCall

suspend fun AppConfiguration.refreshAppConfigSuspended(key: String): Result {
    val response = makeApiCall {
        appConfigAPI.fetchAppConfigurationSuspended(key)
    }
    return when (response.status) {
        Resource.Status.SUCCESS -> {
            handleAppConfigurationResponseSuspended(response.data!!)
            return Result.success()
        }
        Resource.Status.ERROR -> {
            Log.e("${Aggregation.TAG}#refreshAppConfigSuspended", response.error?.localizedDescription)
            Result.error(response.error)
        }
    }
}

private suspend fun AppConfiguration.handleAppConfigurationResponseSuspended(response: AppConfigurationResponse) {
    response.company?.let {
        db.companyConfig().clearSuspended()
        db.companyConfig().insertSuspended(it)
    }

    response.features?.let {
        db.featureConfig().insertAllSuspended(*it.toTypedArray())

        val apiKeys = it.map { featureconfig ->
            featureconfig.key
        }.toList()

        val staleKeys = db.featureConfig().getStaleKeysSuspended(apiKeys.toTypedArray())

        if (staleKeys.isNotEmpty()) {
            db.featureConfig().deleteManySuspended(staleKeys.toTypedArray())
        }
    }

    response.links?.let {
        db.linkConfig().insertAllSuspended(*it.toTypedArray())

        val apiKeys = it.map { linkConfig ->
            linkConfig.key
        }.toList()

        val staleKeys = db.linkConfig().getStaleKeysSuspended(apiKeys.toTypedArray())

        if (staleKeys.isNotEmpty()) {
            db.linkConfig().deleteManySuspended(staleKeys.toTypedArray())
        }
    }
}

suspend fun AppConfiguration.fetchCompanyConfigSuspended(): CompanyConfig? {
    return db.companyConfig().loadSuspended()
}

suspend fun AppConfiguration.fetchCompanyConfigSuspended(query: SimpleSQLiteQuery): CompanyConfig? =
    db.companyConfig().loadByQuerySuspended(query)

suspend fun AppConfiguration.fetchFeatureConfigSuspended(): List<FeatureConfig> {
    return db.featureConfig().loadSuspended()
}

suspend fun AppConfiguration.fetchFeatureConfigSuspended(query: SimpleSQLiteQuery): List<FeatureConfig> =
    db.featureConfig().loadByQuerySuspended(query)

suspend fun AppConfiguration.fetchLinkConfigSuspended(): List<LinkConfig> {
    return db.linkConfig().loadSuspended()
}

suspend fun AppConfiguration.fetchLinkConfigSuspended(query: SimpleSQLiteQuery): List<LinkConfig> =
    db.linkConfig().loadByQuerySuspended(query)
