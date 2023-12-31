package us.frollo.frollosdk.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.model.coredata.appconfiguration.CompanyConfig

@Dao
internal interface CompanyConfigDao {

    @Query("SELECT * FROM company_config LIMIT 1")
    fun load(): LiveData<CompanyConfig?>

    @Query("SELECT * FROM company_config LIMIT 1")
    suspend fun loadSuspended(): CompanyConfig?

    @RawQuery(observedEntities = [CompanyConfig::class])
    fun loadByQuery(queryStr: SupportSQLiteQuery): LiveData<CompanyConfig?>

    @RawQuery(observedEntities = [CompanyConfig::class])
    suspend fun loadByQuerySuspended(queryStr: SupportSQLiteQuery): CompanyConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: CompanyConfig): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuspended(model: CompanyConfig): Long

    @Query("DELETE FROM company_config")
    fun clear()

    @Query("DELETE FROM company_config")
    suspend fun clearSuspended()

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM company_config")
    fun loadRx(): Observable<CompanyConfig>

    @RawQuery(observedEntities = [CompanyConfig::class])
    fun loadByQueryRx(queryStr: SupportSQLiteQuery): Observable<CompanyConfig>
}
