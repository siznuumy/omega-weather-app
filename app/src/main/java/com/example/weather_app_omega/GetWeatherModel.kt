package com.example.weather_app_omega

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


private const val API_KEY = "ac0b593ebf6a4043a48131058230407"

class GetWeatherModel: ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    private fun errorHappen(context: Context, locs: String?) {
        if (locs != null) {
            if (locs.length > 1) {
                Toast.makeText(context, context.getString(R.string.smth_wrong), Toast.LENGTH_LONG).show()
            } else {
                return
            }
        }
    }

    fun checkGPS(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, context.getString(R.string.gps_provider), Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun getData(city: String?, context: Context,
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
        if (!isOnline(context)) {
            Toast.makeText(context, context.getString(R.string.internet), Toast.LENGTH_LONG).show()
            return
        }
        viewModelScope.launch(start = CoroutineStart.DEFAULT) {
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
                    errorHappen(context, city)
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
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val timeUpd = LocalTime.now().format(formatter)
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
//            last_update = mainObject.getJSONObject("location").getString("localtime").toString().substringAfter(" "),
            last_update = timeUpd.toString(),
            temp = mainObject.getJSONObject("current").getDouble("temp_c").toString(),
            date = context.getString(R.string.tomorrow)
        )
        return list
    }
}