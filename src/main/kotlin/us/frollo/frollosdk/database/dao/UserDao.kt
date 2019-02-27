package us.frollo.frollosdk.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import us.frollo.frollosdk.model.coredata.user.User

@Dao
internal interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun load(): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: User): Long

    @Query("DELETE FROM user")
    fun clear()
}