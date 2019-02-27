package us.frollo.frollosdk.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.ProviderAccount
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.ProviderAccountRelation

@Dao
internal interface ProviderAccountDao {

    @Query("SELECT * FROM provider_account")
    fun load(): LiveData<List<ProviderAccount>>

    @Query("SELECT * FROM provider_account WHERE provider_account_id = :providerAccountId")
    fun load(providerAccountId: Long): LiveData<ProviderAccount?>

    @Query("SELECT * FROM provider_account WHERE provider_id = :providerId")
    fun loadByProviderId(providerId: Long): LiveData<List<ProviderAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg models: ProviderAccount): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: ProviderAccount): Long

    @Query("SELECT provider_account_id FROM provider_account WHERE provider_id IN (:providerIds)")
    fun getIdsByProviderIds(providerIds: LongArray): LongArray

    @Query("SELECT provider_account_id FROM provider_account WHERE provider_account_id NOT IN (:apiIds)")
    fun getStaleIds(apiIds: LongArray): List<Long>

    @Query("DELETE FROM provider_account WHERE provider_account_id IN (:providerAccountIds)")
    fun deleteMany(providerAccountIds: LongArray)

    @Query("DELETE FROM provider_account WHERE provider_account_id = :providerAccountId")
    fun delete(providerAccountId: Long)

    @Query("DELETE FROM provider_account")
    fun clear()

    // Relation methods

    @androidx.room.Transaction
    @Query("SELECT * FROM provider_account")
    fun loadWithRelation(): LiveData<List<ProviderAccountRelation>>

    @androidx.room.Transaction
    @Query("SELECT * FROM provider_account WHERE provider_account_id = :providerAccountId")
    fun loadWithRelation(providerAccountId: Long): LiveData<ProviderAccountRelation?>

    @androidx.room.Transaction
    @Query("SELECT * FROM provider_account WHERE provider_id = :providerId")
    fun loadByProviderIdWithRelation(providerId: Long): LiveData<List<ProviderAccountRelation>>
}