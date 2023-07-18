package com.example.weather_app_omega

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

// TODO: Добавить тулбар с названием приложения
//  и окно объяснения зачем вообще нужен пермишен; + облагородить две доп. карточки



private lateinit var locLauncher: FusedLocationProviderClient
private var _lat = mutableStateOf("59.931")
private var _lon = mutableStateOf("31.311")

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

                val viewModel = viewModel<GetWeatherModel>()
//                val dialogQueue = viewModel.visiblePermissionDialogQueue
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

                var bgColor = LightBlueBg
                if (isSystemInDarkTheme()) {
                    bgColor = DarkBlueBg
                }

                LaunchedEffect(_lat.value, _lon.value) {

                    getLoc2(requestPermissionLauncher)

                    viewModel.getData(
                        (_lat.value + "," + _lon.value),
                        applicationContext,
                        daysList,
                        currentDay
                    )
                }
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        getLocation()
                        viewModel.getData(
                            (_lat.value + "," + _lon.value),
                            this,
                            daysList,
                            currentDay
                        )
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
                        itemsIndexed(daysList.value) { _, item ->
                            ListItem(item)
                        }
                    }
                }


            }
        }
    }

    private var requestPermissionLauncher =
    registerForActivityResult(
    ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("perm launcher", "perm granted")
            getLocation()
        } else {
            Log.d("perm launcher", "perm not granted")
            TODO("добавить окно объяснения")
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    @Composable
    fun PermDio() {
        AlertDialog(
            onDismissRequest = {  },
            confirmButton = {
                getLocation()
            },
            title = {
                stringResource(id = R.string.permission)
            },
            text = {
                stringResource(id = R.string.permission_info)
            },
            modifier = Modifier.wrapContentHeight()
        )
    }
//    @Composable
    private fun getLocation() {
        val cts = CancellationTokenSource()

    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) { return }

        val location = locLauncher.getCurrentLocation(Priority.PRIORITY_LOW_POWER, cts.token)
        location.addOnSuccessListener { locRes ->
            if (locRes != null) {
                _lat.value = locRes.latitude.toString()
                _lon.value = locRes.longitude.toString()
                Log.d("location", "lat=${locRes.latitude}, lon=${locRes.longitude}")
            } else {
                Log.d("location", "received null")
            }
        }
    }



    fun getLoc2(requestPermissionLauncher: ActivityResultLauncher<String>) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.d("permission", "perm granted")
                getLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
//                showInContextUI(...)
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                Log.d("permission", "perm not granted")
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }
}








