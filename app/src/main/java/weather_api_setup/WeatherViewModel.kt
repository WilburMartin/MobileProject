package weather_api_setup


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData



class WeatherViewModel (application: Application): AndroidViewModel(application) {

    public var weatherList: MutableLiveData<CityWeather> = MutableLiveData()
    public var weatherRepository: WeatherRepository = WeatherRepository()

    fun getWeatherByCity(city: String) {
        weatherRepository.getWeatherByCity(weatherList, city)
    }
    fun getWeatherByCoords(lat: String, lon: String) {
        weatherRepository.getWeatherByCoords(weatherList, lat, lon)
    }

}