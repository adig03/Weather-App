package com.example.weatherapp

import com.example.weatherapp.Constants.Companion.API_KEY
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appId: String = API_KEY,
        @Query("units") units: String
    ): WeatherApp
}

