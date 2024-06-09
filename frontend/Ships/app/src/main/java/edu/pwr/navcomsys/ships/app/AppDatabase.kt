package edu.pwr.navcomsys.ships.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.pwr.navcomsys.ships.BuildConfig
import edu.pwr.navcomsys.ships.data.database.ChatMessage
import edu.pwr.navcomsys.ships.data.database.User
import edu.pwr.navcomsys.ships.model.datasource.local.ChatMessageDataSource
import edu.pwr.navcomsys.ships.model.datasource.local.UserLocalDataSource

@Database(
    entities = [
        User::class,
        ChatMessage::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userLocalDataSource(): UserLocalDataSource
    abstract fun chatMessageLocalDataSource(): ChatMessageDataSource

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "${BuildConfig.APPLICATION_ID}.AppDatabase"
            ).build()
        }
    }
}