package local_database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LocalActivityViewModel(application:Application):AndroidViewModel(application) {
    var localActivityList: LiveData<List<localactivity>> = MutableLiveData()
    private val repository: LocalActivityRepository

    init {
        repository = LocalActivityRepository(LocalActivityRoomDatabase.getDatabase(application).LocalActivityDao())
        localActivityList = repository.allActivities
    }

    fun getJokes() : LiveData<List<localactivity>> {
        return localActivityList
    }

    fun insert(localactivity: localactivity) {
        repository.insert(localactivity)
    }

    fun clear() {
        repository.clear()
    }



}