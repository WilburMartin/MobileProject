package local_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(localactivity::class), version = 2)
public abstract class LocalActivityRoomDatabase : RoomDatabase() {

    abstract fun LocalActivityDao(): LocalActivityDao

    //singleton pattern
    companion object {

        @Volatile
        private var INSTANCE: LocalActivityRoomDatabase? = null

        fun getDatabase(context: Context) : LocalActivityRoomDatabase {
            val temp = INSTANCE
            if(temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalActivityRoomDatabase::class.java,
                    "joke_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}