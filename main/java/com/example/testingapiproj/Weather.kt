package com.example.testingapiproj


data class WeatherPayload(
    val data: List<CityWeather>

)




data class CityWeather(
    val weather: List<Weather>,
    val main: Main,
    val visibility: String,
    val wind: Wind,
    val name: String

)


data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Double
)

data class Main(
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    val temp_min: Double,
    val temp_max: Double
)
