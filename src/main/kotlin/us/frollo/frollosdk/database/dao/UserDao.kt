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
import androidx.room.Transaction
import io.reactivex.Observable
import us.frollo.frollosdk.model.coredata.user.User
import us.frollo.frollosdk.model.coredata.user.UserRelation

@Dao
internal interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun load(): LiveData<User?>

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun loadSuspended(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: User): Long

    @Query("DELETE FROM user")
    fun clear()

    // Relation methods

    @Transaction
    @Query("SELECT * FROM user LIMIT 1")
    fun loadWithRelation(): LiveData<UserRelation?>

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM user LIMIT 1")
    fun loadRx(): Observable<User?>

    // Relation methods

    @Transaction
    @Query("SELECT * FROM user LIMIT 1")
    fun loadWithRelationRx(): Observable<UserRelation?>
}
