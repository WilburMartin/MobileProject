package places_api_setup

import android.media.Image
import com.google.android.gms.maps.model.LatLng
import data_classes.NearbySearch
import data_classes.place_details
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

        @GET("place/details/json")
        suspend fun locationQuery(
                @Query("place_id") place_id:String,
                @Query("key") key:String,
                @Query("fields") fields:String
        ): Response<place_details>

        @GET("place/photo")
        suspend fun photoQuery (
                @Query("key") key:String,
                @Query("photoreference")photoreference:String,
                @Query("maxheight")maxheight:String
        ):Response<Image>
}

