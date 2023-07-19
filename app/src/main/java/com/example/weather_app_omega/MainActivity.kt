package com.example.weather_app_omega

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
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
import com.example.weather_app_omega.ui.theme.DarkBlue
import com.example.weather_app_omega.ui.theme.DarkBlueBg
import com.example.weather_app_omega.ui.theme.LightBlue
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
private var _lat = mutableStateOf("60.000")
private var _lon = mutableStateOf("30.000")
private lateinit var locationPermissionResultLauncher: ActivityResultLauncher<String>
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

                val scrollState = rememberScrollState()

                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                val ffd = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

//                ffd.isActiveNetworkMetered(IN)

                val viewModel = viewModel<GetWeatherModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

                var bgColor = LightBlueBg
                var bgColor2 = LightBlue
                if (isSystemInDarkTheme()) {
                    bgColor = DarkBlueBg
                    bgColor2 = DarkBlue
                }

                locationPermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {
                        viewModel.onPermissionResult(
                            permission = Manifest.permission.ACCESS_COARSE_LOCATION,
                            isGranted = it
                        )
                        if (viewModel.checkGPS(applicationContext)) {
                            getLocation()
                        }
                    }
                )

                LaunchedEffect(_lat.value, _lon.value) {
                    if (viewModel.checkGPS(applicationContext)) {
                        getLocation()
                    }
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
                        if (viewModel.checkGPS(applicationContext)) {
                            getLocation()
                        }
                        viewModel.getData(
                            (_lat.value + "," + _lon.value),
                            this,
                            daysList,
                            currentDay
                        )
                    }) {
                    LazyColumn(
                        modifier = Modifier
                            .background(bgColor)
                            .fillMaxSize()
//                            .scrollable(state = scrollState, orientation = Orientation.Vertical)
                    ) {
                        item {
                            topLayout(currentDay = currentDay, bgColor2)
                            TESTmidLayout(currentDay, bgColor2)
                            dopLayout(currentDay, bgColor2)
                        }
                        itemsIndexed(daysList.value) { _, item ->
                            ListItem(item, applicationContext, bgColor2)
                        }
                    }
                }

                dialogQueue
                    .reversed()
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                    LocationPermissionTextProvider()
                                }
                                else -> return@forEach
                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            context = applicationContext,
                            onDismiss = { openAppSettings() },
                            onOkClick = {
                                viewModel.dismissDialog()
//                                locationPermissionResultLauncher.launch(
//                                    Manifest.permission.ACCESS_COARSE_LOCATION
//                                )
                            },
                            onGoToAppSettingsClick = { openAppSettings() }
                        )
                    }

            }
        }
    }

    fun Activity.openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }

    @Composable
    fun checkGPS() {
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, stringResource(id = R.string.gps_provider), Toast.LENGTH_SHORT).show()
//        }
    }

    private fun getLocation() {
        val cts_token = CancellationTokenSource().token

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("location", "no permission")
            getPermission()
        }

//        checkGPS()

        val location = locLauncher.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts_token)
        location.addOnSuccessListener { locRes ->
            if (locRes != null) {
                _lat.value = locRes.latitude.toString()
                _lon.value = locRes.longitude.toString()
                Log.d("location", "lat=${locRes.latitude}, lon=${locRes.longitude}")
            } else {
                try {
                    Log.d("location", "received null")
                    val location = locLauncher.lastLocation
                    location.addOnSuccessListener { locRes ->
                        if (locRes != null) {
                            _lat.value = locRes.latitude.toString()
                            _lon.value = locRes.longitude.toString()
                            Log.d("location", "lat=${locRes.latitude}, lon=${locRes.longitude}")
                        }
                    }
                } catch (_: Exception) {

                }
            }
        }
    }


    fun getPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.d("permission", "perm granted")
                getLocation()
            } else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                Log.d("permission", "perm not granted")
                locationPermissionResultLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }
}








