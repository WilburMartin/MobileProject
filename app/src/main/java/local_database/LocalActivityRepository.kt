package local_database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalActivityRepository(private val localActivityDao: LocalActivityDao) {


    val allActivities: LiveData<List<localactivity>> = localActivityDao.getActivities();

    fun insert(localactivity: localactivity) {
        CoroutineScope(Dispatchers.IO).launch {
            localActivityDao.insert(localactivity)
        }
    }

    fun clear() {
        CoroutineScope(Dispatchers.IO).launch {
            localActivityDao.deleteAll()
        }
    }

//    fun delete(localactivity: localactivity) {
//        CoroutineScope(Dispatchers.IO).launch {
//            localActivityDao.delete(localactivity)
//        }
//    }
}
