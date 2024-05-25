package edu.pwr.navcomsys.ships.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.pwr.navcomsys.ships.data.database.User

@Dao
interface UserLocalDataSource {
    @Insert
    suspend fun insert(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User
}