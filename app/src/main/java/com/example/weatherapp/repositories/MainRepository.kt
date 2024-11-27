package com.example.weatherapp.repositories

import com.example.weatherapp.Constants.Companion.API_KEY
import com.example.weatherapp.WeatherApp
import com.example.weatherapp.api.RetrofitInstance
import retrofit2.Response

class MainRepository {

    suspend fun getWeatherData(cityName: String): WeatherApp {
        return RetrofitInstance.api.getWeatherData(cityName, API_KEY, "metric")
    }
}
