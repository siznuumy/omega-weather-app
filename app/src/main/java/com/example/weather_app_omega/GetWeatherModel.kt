package com.example.weather_app_omega

import android.content.Context
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.ByteString.Companion.encodeUtf8
import org.json.JSONObject
import java.util.Locale
import kotlin.coroutines.coroutineContext

private const val API_KEY = "ac0b593ebf6a4043a48131058230407"

class GetWeatherModel: ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun getData(city: String, context: Context,
                daysList: MutableState<List<WeatherData>>,
                currentDay: MutableState<WeatherData>
    ){
        val uurl = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
                "&q=$city" +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"
        val url = "http://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
                "&q=$city" +
                "&days=3" +
                "&aqi=no" +
                "&lang=" + Locale.getDefault().country +
                "&alerts=no" +
                "&tides=no" +
                "&hour=10"
        val queue = Volley.newRequestQueue(context)
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("MyLog", "start of JSON loading")
            val sRequest = StringRequest(
                Request.Method.GET,
                url,
                { response ->
                    val list = getWeatherByDays(response)
                    currentDay.value = list[0]
                    daysList.value = list
                    Log.d("MyLog", "JSON loaded successfully")
                },
                {
                    Log.d("MyLog", "VolleyError: $it")
                }
            )
            queue.add(sRequest)
            _isLoading.value = false
        }
    }

    private fun getWeatherByDays(response: String): List<WeatherData>{
        if (response.isEmpty()) return listOf()
        val list = ArrayList<WeatherData>()
        val mainObject = JSONObject(response)
        var city = mainObject.getJSONObject("location").getString("name")
        val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

        if (city == "Saint Petersburg" && Locale.getDefault().country == "ru") {
            city = "Санкт-Петербург".encodeUtf8().toString()
        } else if (city == "Moscow" && Locale.getDefault().country == "ru") {
            city = "Москва".encodeUtf8().toString()
        }
        for (i in 0 until days.length()){
            val item = days[i] as JSONObject
            list.add(
                WeatherData(
                    location = city,
                    date = item.getString("date"),
                    temp = "",
                    condition = item.getJSONObject("day").getJSONObject("condition").getString("text"),
                    wind_speed = item.getJSONObject("day").getDouble("maxwind_kph").toString(),
                    feels_like = item.getJSONArray("hour").getJSONObject(0).getDouble("feelslike_c").toString(),
                    icon = item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                    max_temp = item.getJSONObject("day").getDouble("maxtemp_c").toString(),
                    min_temp = item.getJSONObject("day").getDouble("mintemp_c").toString(),
                    humidity = item.getJSONObject("day").getInt("avghumidity").toString()

                )
            )
        }
        list[0] = list[0].copy(
            last_update = mainObject.getJSONObject("location").getString("localtime").toString(),
            temp = mainObject.getJSONObject("current").getDouble("temp_c").toString(),
        )
        return list
    }

    @Composable
    fun getResources(_id: Int): String {
        return stringResource(id = _id)
    }
}