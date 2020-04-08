package com.example.testingapiproj


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("weather")
    suspend fun getWeatherByCity(@Query("q") q: String, @Query("appid") apppid: String): Response<CityWeather>

    @GET("weather")
    suspend fun getWeatherByCoords(@Query("lat") lat: String, @Query("lon") lon: String, @Query("appid") apppid: String): Response<CityWeather>

}