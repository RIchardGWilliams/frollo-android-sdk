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
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.model.coredata.cdr.ExternalParty

@Dao
internal interface ExternalPartyDao {

    @Query("SELECT * FROM external_party WHERE party_id = :externalPartyId")
    fun load(externalPartyId: Long): LiveData<ExternalParty?>

    @RawQuery(observedEntities = [ExternalParty::class])
    fun loadByQuery(queryStr: SupportSQLiteQuery): LiveData<List<ExternalParty>>

    @RawQuery
    fun getIdsByQuery(queryStr: SupportSQLiteQuery): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg models: ExternalParty): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: ExternalParty): Long

    @Query("DELETE FROM external_party WHERE party_id IN (:externalPartyIds)")
    fun deleteMany(externalPartyIds: LongArray)

    @Query("DELETE FROM external_party")
    fun clear()

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM external_party WHERE party_id = :externalPartyId")
    fun loadRx(externalPartyId: Long): Observable<ExternalParty?>

    @RawQuery(observedEntities = [ExternalParty::class])
    fun loadByQueryRx(queryStr: SupportSQLiteQuery): Observable<List<ExternalParty>>
}
