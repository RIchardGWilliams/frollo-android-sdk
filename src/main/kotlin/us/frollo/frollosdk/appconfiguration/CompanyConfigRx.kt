package us.frollo.frollosdk.appconfiguration

import androidx.sqlite.db.SimpleSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig

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
