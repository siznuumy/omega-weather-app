package com.example.weather_app_omega

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
//import coil.compose.AsyncImage
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app_omega.ui.theme.LightBlueBg
import com.example.weather_app_omega.ui.theme.Weather_app_omegaTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Weather_app_omegaTheme {
                val state = rememberScrollState()
                LaunchedEffect(Unit) { state.animateScrollTo(0) }
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
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.getData("Saint-Petersburg", this, daysList, currentDay)
                    }) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LightBlueBg)
                    ) {
                        item {
                            topLayout(currentDay = currentDay)
                            TESTmidLayout()
//                        DaysLayout()
                        }

                        items(3) {
                            ListItem()
                        }

                    }
                }

            }
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun ListOfBlocks() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 256.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = WeatherData().temp,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Row() {
                        Text("feels like ")
                        Text(text = "18")
                    }
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "ОЩУЩАЕТСЯ КАК")

                }
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = WeatherData().temp,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Row() {
                        Text("feels like ")
                        Text(text = "18")
                    }
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "ОЩУЩАЕТСЯ КАК")

                }
            }
        }
    }
}








