package weather_api_setup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mobileproject.R

class TempWeatherActivity: AppCompatActivity(){
    lateinit var viewModel: WeatherViewModel
    var questionList: ArrayList<CityWeather> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)

        viewModel!!.weatherList.observe(this, Observer {
            questionList.clear()
            questionList.add(it)

            //centertext.text = questionList.get(0).weather.get(0).description; //For Weather Long Desc
            //centertext.text = questionList.get(0).weather.get(0).main; //For Weather Short Desc
            //centertext.text = questionList.get(0).visibility; //For Visibility

        })

        viewModel.getWeatherByCity("London");
    }
}
