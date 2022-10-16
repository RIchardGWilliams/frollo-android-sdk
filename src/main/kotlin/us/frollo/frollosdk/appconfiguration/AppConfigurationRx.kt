package us.frollo.frollosdk.appconfiguration

import androidx.sqlite.db.SimpleSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig

/**
 * Fetch company config from the cache
 *
 * @return Rx Observable object of List<CompanyConfig> which can be observed using an Observer for future changes as well.
 */
fun AppConfiguration.fetchCompanyConfigRx(): Observable<CompanyConfig> {
    return db.companyConfig().loadRx()
}

/**
 * Advanced method to fetch company config by SQL query from the cache
 *
 * @param query SimpleSQLiteQuery: Select query which fetches company config from the cache
 *
 * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
 *
 * @return Rx Observable object of List<CompanyConfig> which can be observed using an Observer for future changes as well.
 */
fun AppConfiguration.fetchCompanyConfigRx(query: SimpleSQLiteQuery): Observable<CompanyConfig> {
    return db.companyConfig().loadByQueryRx(query)
}

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

/**
 * Fetch list of app link configs from the cache
 *
 * @return Rx Observable object of List<LinkConfig> which can be observed using an Observer for future changes as well.
 */
fun AppConfiguration.fetchLinkConfigRx(): Observable<List<LinkConfig>> {
    return db.linkConfig().loadRx()
}

/**
 * Advanced method to fetch list of app link configs by SQL query from the cache
 *
 * @param query SimpleSQLiteQuery: Select query which fetches link configs from the cache
 *
 * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
 *
 * @return Rx Observable object of List<LinkConfig> which can be observed using an Observer for future changes as well.
 */
fun AppConfiguration.fetchLinkConfigRx(query: SimpleSQLiteQuery): Observable<List<LinkConfig>> {
    return db.linkConfig().loadByQueryRx(query)
}
