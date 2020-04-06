package com.example.testingapiproj

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class TriviaViewModel (application: Application): AndroidViewModel(application) {

    public var categoryList: MutableLiveData<CityWeather> = MutableLiveData()
    public var triviaRepository: TriviaRepository = TriviaRepository()

    fun getCategories(city: String) {
        triviaRepository.getCategories(categoryList, city)
    }

}