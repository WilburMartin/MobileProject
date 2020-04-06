package com.example.testingapiproj

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class TriviaRepository {

    //api.openweathermap.org/data/2.5/forecast/daily?q=London&appid=b1b15e88fa797225412429c1c50c122a1

    //http://samples.openweathermap.org/data/2.5/weather?q=London&appid=41ea70c5e4ca535afec0eae8933303f1

    val id = "41ea70c5e4ca535afec0eae8933303f1"

    val service = ApiClient.makeRetrofitService()

    fun getCategories(resBody: MutableLiveData<CityWeather>, city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getCategories(city, id)

            withContext(Dispatchers.Main) {
                try{
                    if(response.isSuccessful) {
                        resBody.value = response.body()
                    }
                } catch (e: HttpException) {
                    println("Http error")
                }
            }
        }
    }


}