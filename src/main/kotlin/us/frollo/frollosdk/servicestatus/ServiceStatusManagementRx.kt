/*
 * Copyright 2019 Frollo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.frollo.frollosdk.servicestatus

import androidx.sqlite.db.SimpleSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutage

/**
 * Fetch all outages from the cache
 *
 * @return Rx Observable object of List<ServiceOutage> which can be observed using an Observer for future changes as well.
 */
fun ServiceStatusManagement.fetchServiceOutagesRx(): Observable<List<ServiceOutage>> {
    return db.serviceOutages().loadRx()
}

/**
 * Advanced method to fetch outages by SQL query from the cache
 *
 * @param query SimpleSQLiteQuery: Select query which fetches outages from the cache
 *
 * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
 *
 * @return Rx Observable object of List<ServiceOutage> which can be observed using an Observer for future changes as well.
 */
fun ServiceStatusManagement.fetchServiceOutagesRx(query: SimpleSQLiteQuery): Observable<List<ServiceOutage>> {
    return db.serviceOutages().loadByQueryRx(query)
}
