package local_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_activities")
data class localactivity (
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "address")
    val address: String,
    val type: String

)
{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
