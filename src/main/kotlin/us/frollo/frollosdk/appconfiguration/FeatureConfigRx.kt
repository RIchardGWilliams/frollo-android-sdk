package us.frollo.frollosdk.appconfiguration

import androidx.sqlite.db.SimpleSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig

/**
 * Fetch list of app feature configs from the cache
 *
 * @return Rx Observable object of List<FeatureConfig> which can be observed using an Observer for future changes as well.
 */
fun AppConfiguration.fetchFeatureConfigRx(): Observable<List<FeatureConfig>> {
    return db.featureConfig().loadRx()
}

/**
 * Advanced method to fetch list of app feature configs by SQL query from the cache
 *
 * @param query SimpleSQLiteQuery: Select query which fetches feature configs from the cache
 *
 * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
 *
 * @return Rx Observable object of List<FeatureConfig> which can be observed using an Observer for future changes as well.
 */
fun AppConfiguration.fetchFeatureConfigRx(query: SimpleSQLiteQuery): Observable<List<FeatureConfig>> {
    return db.featureConfig().loadByQueryRx(query)
}
