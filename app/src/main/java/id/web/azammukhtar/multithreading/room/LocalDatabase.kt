package id.web.azammukhtar.multithreading.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataModel::class], version = 6, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun taskDao(): DataDao

    companion object {

        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: createDatabase(context).also { INSTANCE = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, "testApp.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}