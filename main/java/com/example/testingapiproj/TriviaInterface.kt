package com.example.testingapiproj


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaInterface {

    @GET("weather")
    suspend fun getCategories(@Query("q") q: String, @Query("appid") apppid: String): Response<CityWeather>

}