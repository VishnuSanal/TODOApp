package phone.vishnu.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import phone.vishnu.todoapp.dao.ShelveDao
import phone.vishnu.todoapp.model.Shelve

@Database(entities = [Shelve::class], version = 1)
abstract class ShelveDatabase : RoomDatabase() {
    abstract fun shelveDao(): ShelveDao

    companion object {
        @Volatile
        private var INSTANCE: ShelveDatabase? = null

        fun getDatabase(context: Context): ShelveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShelveDatabase::class.java,
                    "shelve_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}