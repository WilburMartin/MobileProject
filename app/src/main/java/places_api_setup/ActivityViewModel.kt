package places_api_setup

import android.app.Application
import android.media.Image
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import data_classes.NearbySearch
import data_classes.Photo
import data_classes.place_details

class ActivityViewModel(application: Application): AndroidViewModel(application) {

    //live data and repository to track requests
    public var placesList: MutableLiveData<NearbySearch> = MutableLiveData()
    public var placesRepository: PlacesRepository = PlacesRepository()
    public var singlePlace :MutableLiveData<place_details> = MutableLiveData()
    public var singlePhoto : MutableLiveData<Image> = MutableLiveData()

    //request to get populare breweries
    fun getNearbySearch(location: String, radius:String, type: String, key: String) {
        placesRepository.getNearbySearch(placesList, location, radius, type, key)
    }

    fun locationQuery(placeid:String, key:String, fields:String) {
        placesRepository.locationQuery(singlePlace,placeid, key, fields)
    }

    fun photoQuery(key: String, photoreference: String, maxheight:String) {
        placesRepository.photoQuery(singlePhoto, key, photoreference, maxheight)
    }

}
