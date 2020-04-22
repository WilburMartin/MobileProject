package local_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface LocalActivityDao {
    @Query("SELECT * FROM local_activities")
    fun getActivities(): LiveData<List<localactivity>>

    @Insert
    fun insert(local: localactivity)

    @Delete
    fun delete(local: localactivity)

    @Query("DELETE FROM local_activities")
    fun deleteAll()
    

}