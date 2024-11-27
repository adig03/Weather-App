package com.example.weatherapp

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ViewModels.AppViewModel
import com.example.weatherapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appViewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and bind the views
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ViewModel
        appViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        // Observe the weatherData LiveData for updates
        appViewModel.weatherData.observe(this, Observer { weatherResponse ->
            weatherResponse?.let {
                updateUI(weatherResponse)
            }
        })

        // Fetch weather data for a default city (example: "Jaipur")
        fetchWeatherData("Jaipur")

        // Initialize Search functionality
        searchCity()
    }

    private fun fetchWeatherData(cityName: String) {
        appViewModel.fetchWeather(cityName)
        binding.cityName.text = cityName  // Update the city name on the UI
    }

    // Update UI based on the received weather data
    private fun updateUI(weatherResponse: WeatherApp) {
        val temperature = weatherResponse.main.temp.toString()
        val humidity = weatherResponse.main.humidity.toString()
        val windSpeed = weatherResponse.wind.speed
        val sunRise = weatherResponse.sys.sunrise.toLong()
        val sunSet = weatherResponse.sys.sunset.toLong()
        val seaLevel = weatherResponse.main.pressure
        val condition = weatherResponse.weather.firstOrNull()?.main ?: "unknown"
        val maxTemp = weatherResponse.main.temp_max
        val minTemp = weatherResponse.main.temp_min

        // Update UI elements
        binding.temperature.text = "$temperature °C"
        binding.weather.text = condition
        binding.maxTemp.text = "Max Temp: $maxTemp °C"
        binding.minTemp.text = "Min Temp: $minTemp °C"
        binding.humidity.text = "$humidity %"
        binding.windspeed.text = "$windSpeed m/s"
        binding.sunrise.text = "${time(sunRise)}"
        binding.sunset.text = "${time(sunSet)}"
        binding.sealevel.text = "$seaLevel hPa"
        binding.condition.text = condition
        binding.day.text = dayName(System.currentTimeMillis())
        binding.date.text = date()

        // Change background and animation based on weather condition
        changeImageAccordingWeatherCondition(condition)
    }

    // Convert timestamp to time in HH:mm format
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    // Get the day name (e.g., Monday)
    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    // Get the current date in dd MMMM yyyy format
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    // Change background and animation according to the weather condition
    private fun changeImageAccordingWeatherCondition(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    // Setup search functionality
    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    fetchWeatherData(query)  // Fetch weather data for the entered city
                    searchView.setQuery("", false)  // Clear the search field
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally, handle any text changes in the search view here
                return true
            }
        })
    }
}
