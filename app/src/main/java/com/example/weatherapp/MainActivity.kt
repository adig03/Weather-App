package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ViewModels.AppViewModel
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appViewModel: AppViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        appViewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        appViewModel.weatherData.observe(this, Observer { weatherResponse ->
            weatherResponse?.let {
                updateUI(weatherResponse)
            }
        })


        fetchWeatherData("Una")


        searchCity()


        fetchDeviceLocation()
    }


    private fun fetchWeatherData(cityName: String) {
        appViewModel.fetchWeather(cityName)
        binding.cityName.text = cityName
    }


    private fun fetchDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val cityName = getCityNameFromLocation(it.latitude, it.longitude)
                        Toast.makeText(this, "Current Location: $cityName", Toast.LENGTH_LONG).show()
                        fetchWeatherData(cityName)
                    }
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }


    private fun getCityNameFromLocation(latitude: Double, longitude: Double): String {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                // Prioritize locality (city) specifically
                val city = addresses[0].locality
                return city ?: "Unknown Location"
            } else {
                return "Unknown Location"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Unknown Location"
        }
    }


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


        changeImageAccordingWeatherCondition(condition)
    }


    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }


    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

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


    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    fetchWeatherData(query)
                    searchView.setQuery("", false)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
}
