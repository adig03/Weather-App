package com.example.weatherapp.api

import com.example.weatherapp.ApiInterface
import com.example.weatherapp.Constants.Companion.API_KEY
import com.example.weatherapp.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {


    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val api: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}
