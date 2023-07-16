package com.example.weather_app_omega

import android.content.Context
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.icu.text.UnicodeFilter
import android.icu.text.UnicodeSet
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather_app_omega.ui.theme.LightBlue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encode
import okio.ByteString.Companion.encodeUtf8
import okio.utf8Size
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
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
                    val list = getWeatherByDays(response, context)
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

    private fun getWeatherByDays(response: String, context: Context): List<WeatherData>{
        if (response.isEmpty()) return listOf()
        val list = ArrayList<WeatherData>()
        val mainObject = JSONObject(response)
        var city = mainObject.getJSONObject("location").getString("name")
        val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

        if (city == "Saint Petersburg") {
            city = context.getString(R.string.saint_p)
        } else if (city == "Moscow") {
            city = context.getString(R.string.msk)
        } else {
            city = city.format(locale = Locale.getDefault())
        }
        for (i in 0 until days.length()){
            val item = days[i] as JSONObject
            list.add(
                WeatherData(
                    location = city,
                    date = LocalDate.parse(item.getString("date").toString()).dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).toString(),
                    temp = "",
//                    cyrillic text displaying incorrectly without this messy encode-decode process
                    condition = URLDecoder.decode(
                        URLEncoder.encode(
                            item.getJSONObject("day").getJSONObject("condition").getString("text"),
                            "iso8859-1"),
                        "UTF-8"),
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
            last_update = mainObject.getJSONObject("location").getString("localtime").toString().substringAfter(" "),
            temp = mainObject.getJSONObject("current").getDouble("temp_c").toString(),
            date = context.getString(R.string.tomorrow)
        )
        return list
    }
}