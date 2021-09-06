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
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutage

@Dao
internal interface ServiceOutageDao {

    @Query("SELECT * FROM service_outage")
    fun load(): LiveData<List<ServiceOutage>>

    @RawQuery(observedEntities = [ServiceOutage::class])
    fun loadByQuery(queryStr: SupportSQLiteQuery): LiveData<List<ServiceOutage>>

    @RawQuery
    fun find(queryStr: SupportSQLiteQuery): List<ServiceOutage>

    @Query("UPDATE service_outage SET read = 1 WHERE outage_id == :outageId")
    fun markOutageAsRead(outageId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg models: ServiceOutage): LongArray

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg model: ServiceOutage): Int

    @Query("SELECT outage_id FROM service_outage")
    fun getIds(): List<Long>

    @Query("DELETE FROM service_outage WHERE outage_id IN (:outageIds)")
    fun deleteMany(outageIds: LongArray)

    @Query("DELETE FROM service_outage")
    fun clear()

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM service_outage")
    fun loadRx(): Observable<List<ServiceOutage>>

    @RawQuery(observedEntities = [ServiceOutage::class])
    fun loadByQueryRx(queryStr: SupportSQLiteQuery): Observable<List<ServiceOutage>>
}
