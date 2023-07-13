package com.example.weather_app_omega

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

//private const val API_KEY = "ac0b593ebf6a4043a48131058230407"
//
//fun getData(city: String, context: Context,
//                    daysList: MutableState<List<WeatherData>>,
//                    currentDay: MutableState<WeatherData>){
//    val uurl = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
//            "&q=$city" +
//            "&days=" +
//            "3" +
//            "&aqi=no&alerts=no"
//    val url = "http://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
//            "&q=$city" +
//            "&days=3" +
//            "&aqi=no" +
//            "&lang=" + R.string.url_lang +
//            "&alerts=no" +
//            "&tides=no" +
//            "&hour=10"
//    val queue = Volley.newRequestQueue(context)
//    val sRequest = StringRequest(
//        Request.Method.GET,
//        url,
//        {
//                response ->
//            val list = getWeatherByDays(response)
//            currentDay.value = list[0]
//            daysList.value = list
//            print(response)
//        },
//        {
//            Log.d("MyLog", "VolleyError: $it")
//        }
//    )
//    queue.add(sRequest)
//}
//
//private fun getWeatherByDays(response: String): List<WeatherData>{
//    if (response.isEmpty()) return listOf()
//    val list = ArrayList<WeatherData>()
//    val mainObject = JSONObject(response)
//    var city = mainObject.getJSONObject("location").getString("name")
//    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
//
//    if (city == "Saint Petersburg") {
//        city = R.string.saint_p.toString()
//    } else if (city == "Moscow") {
//        city = R.string.msk.toString()
//    }
//    for (i in 0 until days.length()){
//        val item = days[i] as JSONObject
//        list.add(
//            WeatherData(
//                city,
//                item.getString("date"),
//                "",
//                item.getJSONObject("day").getJSONObject("condition")
//                    .getString("text"),
//                item.getJSONObject("day").getDouble("maxwind_kph").toString(),
//                item.getJSONObject("hour").getDouble("feelslike_c").toString(),
//                item.getJSONObject("day").getJSONObject("condition")
//                    .getString("icon"),
//                item.getJSONObject("day").getDouble("maxtemp_c").toString(),
//                item.getJSONObject("day").getDouble("mintemp_c").toString(),
//                item.getJSONObject("day").getInt("avghumidity").toString()
//
//            )
//        )
//    }
//    list[0] = list[0].copy(
//        last_update = mainObject.getJSONObject("current").getString("last_updated"),
//        temp = mainObject.getJSONObject("current").getDouble("temp_c").toString(),
//    )
//    return list
//}