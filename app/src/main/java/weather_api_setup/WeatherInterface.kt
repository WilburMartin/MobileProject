package weather_api_setup
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {


        @GET("weather")
        suspend fun getCategories(@Query("q") q: String, @Query("appid") apppid: String): Response<CityWeather>
    }
