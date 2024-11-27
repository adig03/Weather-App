package com.example.weatherapp.ViewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.WeatherApp
import com.example.weatherapp.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository()

    val weatherData = MutableLiveData<WeatherApp>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchWeather(cityName: String) {
        isLoading.value = true

        viewModelScope.launch {
            try {

                val weather = repository.getWeatherData(cityName)


                weatherData.value = weather


            } catch (e: Exception) {

                errorMessage.value = "Error: ${e.message}"
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
                Log.e("AppViewModel", "Error: ${e.message}", e)
            } finally {

                isLoading.value = false
            }
        }
    }
}
