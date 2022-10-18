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

package us.frollo.frollosdk.consents

import androidx.sqlite.db.SimpleSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.base.SimpleSQLiteQueryBuilder
import us.frollo.frollosdk.extensions.sqlForConsents
import us.frollo.frollosdk.model.coredata.cdr.CDRConfiguration
import us.frollo.frollosdk.model.coredata.cdr.Consent
import us.frollo.frollosdk.model.coredata.cdr.ConsentRelation
import us.frollo.frollosdk.model.coredata.cdr.ConsentStatus

/**
 * Fetch consent by ID from the cache
 *
 * @param consentId Unique consent ID to fetch
 *
 * @return Rx Observable object of Consent which can be observed using an Observer for future changes as well.
 */
fun Consents.fetchConsentRx(consentId: Long): Observable<Consent?> {
    return db.consents().loadRx(consentId)
}

/**
 * Fetch consents from the cache
 *
 * @param providerId Filter by associated provider ID of the consent (optional)
 * @param providerAccountId Filter by associated provider account ID of the consent (optional)
 * @param status Filter by the status of the consent (optional)
 *
 * @return Rx Observable object of List<Consent> which can be observed using an Observer for future changes as well.
 */
fun Consents.fetchConsentsRx(
    providerId: Long? = null,
    providerAccountId: Long? = null,
    status: ConsentStatus? = null
): Observable<List<Consent>> {
    return db.consents().loadByQueryRx(sqlForConsents(providerId = providerId, providerAccountId = providerAccountId, status = status))
}

/**
 * Advanced method to fetch consents by SQL query from the cache
 *
 * @param query SimpleSQLiteQuery: Select query which fetches consents from the cache
 *
 * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
 *
 * @return Rx Observable object of List<Consent> which can be observed using an Observer for future changes as well.
 */
fun Consents.fetchConsentsRx(query: SimpleSQLiteQuery): Observable<List<Consent>> {
    return db.consents().loadByQueryRx(query)
}

/**
 * Fetch consent by ID from the cache along with other associated data.
 *
 * @param consentId Unique consent ID to fetch
 *
 * @return Rx Observable object of ConsentRelation which can be observed using an Observer for future changes as well.
 */
fun Consents.fetchConsentWithRelationRx(consentId: Long): Observable<ConsentRelation?> {
    return db.consents().loadWithRelationRx(consentId)
}

/**
 * Fetch consents from the cache along with other associated data.
 *
 * @param providerId Filter by associated provider ID of the consent (optional)
 * @param providerAccountId Filter by associated provider account ID of the consent (optional)
 * @param status Filter by the status of the consent (optional)
 *
 * @return Rx Observable object of List<ConsentRelation> which can be observed using an Observer for future changes as well.
 */
fun Consents.fetchConsentsWithRelationRx(
    providerId: Long? = null,
    providerAccountId: Long? = null,
    status: ConsentStatus? = null
): Observable<List<ConsentRelation>> {
    return db.consents().loadByQueryWithRelationRx(sqlForConsents(providerId = providerId, providerAccountId = providerAccountId, status = status))
}

/**
 * Advanced method to fetch consents by SQL query from the cache along with other associated data.
 *
 * @param query SimpleSQLiteQuery: Select query which fetches consents from the cache
 *
 * Note: Please check [SimpleSQLiteQueryBuilder] to build custom SQL queries
 *
 * @return Rx Observable object of List<ConsentRelation>> which can be observed using an Observer for future changes as well.
 */
fun Consents.fetchConsentsWithRelationRx(query: SimpleSQLiteQuery): Observable<List<ConsentRelation>> {
    return db.consents().loadByQueryWithRelationRx(query)
}

/**
 * Fetch CDR Configuration from the cache
 *
 * @return Rx Observable object of CDRConfiguration which can be observed using an Observer for future changes as well.
 */
fun Consents.fetchCDRConfigurationRx(): Observable<CDRConfiguration?> {
    return db.cdrConfiguration().loadRx()
}
