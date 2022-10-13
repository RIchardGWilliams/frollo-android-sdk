package us.frollo.frollosdk.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.model.coredata.appconfiguration.LinkConfig

@Dao
internal interface LinkConfigDao {

    @Query("SELECT * FROM link_config")
    fun load(): LiveData<List<LinkConfig>>

    @RawQuery(observedEntities = [LinkConfig::class])
    fun loadByQuery(queryStr: SupportSQLiteQuery): LiveData<List<LinkConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: LinkConfig): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg models: LinkConfig): LongArray

    @Query("DELETE FROM link_config WHERE `key` = :key")
    fun delete(key: String)

    @Query("DELETE FROM link_config")
    fun clear()

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM link_config")
    fun loadRx(): Observable<List<LinkConfig>>

    @RawQuery(observedEntities = [LinkConfig::class])
    fun loadByQueryRx(queryStr: SupportSQLiteQuery): Observable<List<LinkConfig>>
}
