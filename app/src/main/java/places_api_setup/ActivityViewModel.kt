package places_api_setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import data_classes.NearbySearch

class ActivityViewModel(application: Application): AndroidViewModel(application) {

    //live data and repository to track requests
    public var placesList: MutableLiveData<NearbySearch> = MutableLiveData()
    public var placesRepository: PlacesRepository = PlacesRepository()

    //request to get populare breweries
    fun getNearbySearch(location: String, radius:String, type: String, key: String) {
        placesRepository.getNearbySearch(placesList, location, radius, type, key)
    }
}
