package com.example.weather_app_omega

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app_omega.ui.theme.DarkBlueBg
import com.example.weather_app_omega.ui.theme.LightBlueBg
import com.example.weather_app_omega.ui.theme.Weather_app_omegaTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait


private lateinit var locLauncher: FusedLocationProviderClient
private var _lat = mutableStateOf("")
private var _lon = mutableStateOf("")
var loloc = false
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Weather_app_omegaTheme {
                locLauncher = LocationServices.getFusedLocationProviderClient(this)
                val daysList = remember {
                    mutableStateOf(listOf<WeatherData>())
                }
                val currentDay = remember {
                    mutableStateOf(
                        WeatherData(
                        "",
                        "",
                        "0.0",
                        "",
                        "",
                        "0.0",
                        "",
                        "0.0",
                        "0.0",
                        "",
                        ""
                        )
                    )
                }
                val locUploaded = remember {
                    mutableStateOf(loloc)
                }

                val viewModel = viewModel<GetWeatherModel>()
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

                var bgColor = LightBlueBg
                if (isSystemInDarkTheme()) { bgColor = DarkBlueBg }

                getLocation(locUploaded)
                LaunchedEffect(_lat.value, _lon.value) {
                    viewModel.getData((_lat.value+","+_lon.value), applicationContext, daysList, currentDay)
                }

                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.getData((_lat.value+","+_lon.value), this, daysList, currentDay)
                    }) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgColor)
                    ) {
                        item {
                            topLayout(currentDay = currentDay)
                            TESTmidLayout(currentDay)
                            dopLayout()
                        }
                        itemsIndexed(daysList.value) {
                            _, item -> ListItem(item)
                        }
                    }
                }

            }
        }
    }

    private fun getLocation(locUploaded: MutableState<Boolean>) {
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "perm not granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, ),100)
        }

        val location = locLauncher.lastLocation
        location.addOnSuccessListener() {
            if (it != null) {
                _lat.value = it.latitude.toString()
                _lon.value = it.longitude.toString()
                loloc = true
                Log.d("permissions", "lat=${it.latitude}, lon=${it.longitude}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permissions", "perm not granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, ),100)
        }
    }
}







