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
import us.frollo.frollosdk.model.coredata.cdr.DisclosureConsent

@Dao
internal interface DisclosureConsentDao {

    @Query("SELECT * FROM disclosure_consent WHERE consent_id = :consentId")
    fun load(consentId: Long): LiveData<DisclosureConsent?>

    @RawQuery(observedEntities = [DisclosureConsent::class])
    fun loadByQuery(queryStr: SupportSQLiteQuery): LiveData<List<DisclosureConsent>>

    @RawQuery
    fun getIdsByQuery(queryStr: SupportSQLiteQuery): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg models: DisclosureConsent): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: DisclosureConsent): Long

    @Query("DELETE FROM disclosure_consent WHERE consent_id IN (:disclosureConsentIds)")
    fun deleteMany(disclosureConsentIds: LongArray)

    @Query("DELETE FROM disclosure_consent")
    fun clear()

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM disclosure_consent WHERE consent_id = :consentId")
    fun loadRx(consentId: Long): Observable<DisclosureConsent?>

    @RawQuery(observedEntities = [DisclosureConsent::class])
    fun loadByQueryRx(queryStr: SupportSQLiteQuery): Observable<List<DisclosureConsent>>
}
