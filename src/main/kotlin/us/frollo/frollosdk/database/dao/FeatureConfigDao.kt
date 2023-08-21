package us.frollo.frollosdk.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Observable
import us.frollo.frollosdk.model.coredata.appconfiguration.FeatureConfig

@Dao
internal interface FeatureConfigDao {

    @Query("SELECT * FROM feature_config")
    fun load(): LiveData<List<FeatureConfig>>

    @Query("SELECT * FROM feature_config")
    suspend fun loadSuspended(): List<FeatureConfig>

    @RawQuery(observedEntities = [FeatureConfig::class])
    fun loadByQuery(queryStr: SupportSQLiteQuery): LiveData<List<FeatureConfig>>

    @RawQuery(observedEntities = [FeatureConfig::class])
    suspend fun loadByQuerySuspended(queryStr: SupportSQLiteQuery): List<FeatureConfig>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg models: FeatureConfig): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSuspended(vararg models: FeatureConfig): LongArray

    @Query("SELECT `key` FROM feature_config WHERE `key` NOT IN (:apiKeys)")
    fun getStaleKeys(apiKeys: Array<String>): List<String>

    @Query("SELECT `key` FROM feature_config WHERE `key` NOT IN (:apiKeys)")
    suspend fun getStaleKeysSuspended(apiKeys: Array<String>): List<String>

    @Query("DELETE FROM feature_config WHERE `key` IN (:keys)")
    fun deleteMany(keys: Array<String>)

    @Query("DELETE FROM feature_config WHERE `key` IN (:keys)")
    suspend fun deleteManySuspended(keys: Array<String>)

    @Query("DELETE FROM feature_config")
    fun clear()

    @Query("DELETE FROM feature_config")
    suspend fun clearSuspended()

    /**
     * RxJava Return Types
     */

    @Query("SELECT * FROM feature_config")
    fun loadRx(): Observable<List<FeatureConfig>>

    @RawQuery(observedEntities = [FeatureConfig::class])
    fun loadByQueryRx(queryStr: SupportSQLiteQuery): Observable<List<FeatureConfig>>
}
