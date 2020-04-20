package places_api_setup

import com.google.android.gms.maps.model.LatLng
import data_classes.NearbySearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface PlacesInterface {

        //search nearby places by parameters
        @GET("place/nearbysearch/json")
        suspend fun getNearbySearch(
                @Query("location") location: String,
                @Query("radius") radius: String,
                @Query("keyword") keyword: String,
                @Query("key") key: String
        ):Response<NearbySearch>
}

