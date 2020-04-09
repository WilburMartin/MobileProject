package places_api_setup

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//base retrofit client to define settings and url
object PlacesClient {
    const val BASE_URL = "https://maps.googleapis.com/maps/api/"


    fun makeRetrofitService(): PlacesInterface{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient().newBuilder().build())
            .build().create(PlacesInterface::class.java)
    }
}