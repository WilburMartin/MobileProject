package com.example.testingapiproj

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    const val BASE_URL = "http://samples.openweathermap.org/data/2.5/"

    fun makeRetrofitService(): TriviaInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(TriviaInterface::class.java)

    }
}