package weather_api_setup


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class WeatherViewModel (application: Application): AndroidViewModel(application) {

    public var weatherList: MutableLiveData<CityWeather> = MutableLiveData()
    public var weatherRepository: WeatherRepository = WeatherRepository()

    fun getCategories(city: String) {
        weatherRepository.getCategories(weatherList, city)
    }

}