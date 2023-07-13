package com.example.weather_app_omega

data class WeatherData(
    val location: String = "Russia, Moscow",
    val date: String = "29, April 2021",
    val temp: String = "21.0",
    val condition: String = "Sunny",
    val wind_speed: String = "6.8",
    val feels_like: String = "21.0",
    val icon: String = "//cdn.weatherapi.com/weather/64x64/day/113.png",
    val max_temp: String = "11",
    val min_temp: String = "33",
    val humidity: String = "68%",
    val last_update: String = "2023-07-06 22:44"
)