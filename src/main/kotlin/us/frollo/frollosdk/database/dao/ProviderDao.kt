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

package us.frollo.frollosdk.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.model.api.aggregation.providers.ProvidersResponse
import us.frollo.frollosdk.model.coredata.aggregation.providers.Provider
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderRelation

@Dao
internal interface ProviderDao {

    @Query("SELECT * FROM provider")
    fun load(): LiveData<List<Provider>>

    @Query("SELECT * FROM provider WHERE provider_id = :providerId")
    fun load(providerId: Long): LiveData<Provider?>

    @RawQuery(observedEntities = [Provider::class])
    fun loadByQuery(queryStr: SupportSQLiteQuery): LiveData<List<Provider>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg models: Provider): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: Provider): Long

    @Update(entity = Provider::class)
    fun update(vararg models: ProvidersResponse)

    @Query("SELECT provider_id FROM provider")
    fun getIds(): List<Long>

    @Query("SELECT provider_id FROM provider WHERE provider_status NOT IN ('DISABLED','UNSUPPORTED')")
    fun getIdsByStatus(): List<Long>

    @Query("SELECT * FROM provider WHERE provider_id IN (:providerIds)")
    fun fetchProvidersByIds(providerIds: LongArray): LiveData<List<Provider>>

    @Query("DELETE FROM provider WHERE provider_id IN (:providerIds)")
    fun deleteMany(providerIds: LongArray)

    @Query("DELETE FROM provider WHERE provider_id = :providerId")
    fun delete(providerId: Long)

    @Query("DELETE FROM provider")
    fun clear()

    // Relation methods

    @androidx.room.Transaction
    @Query("SELECT * FROM provider")
    fun loadWithRelation(): LiveData<List<ProviderRelation>>

    @androidx.room.Transaction
    @Query("SELECT * FROM provider WHERE provider_id = :providerId")
    fun loadWithRelation(providerId: Long): LiveData<ProviderRelation?>

    @androidx.room.Transaction
    @RawQuery(observedEntities = [Provider::class])
    fun loadByQueryWithRelation(queryStr: SupportSQLiteQuery): LiveData<List<ProviderRelation>>

    @androidx.room.Transaction
    @Query("SELECT * FROM provider WHERE provider_id IN (:providerIds)")
    fun fetchProvidersByIdsWithRelation(providerIds: LongArray): LiveData<List<ProviderRelation>>

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM provider")
    fun loadRx(): Observable<List<Provider>>

    @Query("SELECT * FROM provider WHERE provider_id = :providerId")
    fun loadRx(providerId: Long): Observable<Provider?>

    @RawQuery(observedEntities = [Provider::class])
    fun loadByQueryRx(queryStr: SupportSQLiteQuery): Observable<List<Provider>>

    // Relation methods

    @androidx.room.Transaction
    @Query("SELECT * FROM provider")
    fun loadWithRelationRx(): Observable<List<ProviderRelation>>

    @androidx.room.Transaction
    @Query("SELECT * FROM provider WHERE provider_id = :providerId")
    fun loadWithRelationRx(providerId: Long): Observable<ProviderRelation?>

    @androidx.room.Transaction
    @RawQuery(observedEntities = [Provider::class])
    fun loadByQueryWithRelationRx(queryStr: SupportSQLiteQuery): Observable<List<ProviderRelation>>

    @androidx.room.Transaction
    @Query("SELECT * FROM provider WHERE provider_id IN (:providerIds)")
    fun fetchProvidersByIdsRx(providerIds: LongArray): Observable<List<Provider>>

    @androidx.room.Transaction
    @Query("SELECT * FROM provider WHERE provider_id IN (:providerIds)")
    fun fetchProvidersByIdsWithRelationRx(providerIds: LongArray): Observable<List<ProviderRelation>>
}
