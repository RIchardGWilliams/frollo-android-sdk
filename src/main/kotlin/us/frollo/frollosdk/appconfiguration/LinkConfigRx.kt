package us.frollo.frollosdk.appconfiguration

import androidx.sqlite.db.SimpleSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig

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
