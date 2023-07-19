package com.example.weather_app_omega

import android.content.Context
import android.content.res.Resources
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_app_omega.ui.theme.DarkBlue
import com.example.weather_app_omega.ui.theme.LightBlue
import com.example.weather_app_omega.ui.theme.Weather_app_omegaTheme
import okhttp3.internal.wait
import okio.ByteString.Companion.encodeUtf8
import kotlin.coroutines.coroutineContext

//@Preview(showBackground = true)
@Composable
fun TESTmidLayout(currentDay: MutableState<WeatherData>, bgColor: Color) {
    Column(
        modifier = Modifier
            .padding(15.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
//            elevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "http:" + currentDay.value.icon,
                    contentDescription = "main_temp",
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            end = 8.dp
                        )
                        .size(150.dp)
                )
                Text(
                    text = currentDay.value.location,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
                Text(
                    text = currentDay.value.temp + "ºC",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )

                Text(
                    text = stringResource(id = R.string.feels_like) + ": "
                            + currentDay.value.feels_like + "ºC",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )

                Text(
                    text = stringResource(id = R.string.min) + ": ${currentDay.value.
                    min_temp}º " + stringResource(id = R.string.max)+ ": ${currentDay.value
                        .max_temp}º",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topLayout(currentDay: MutableState<WeatherData>, bgColor: Color) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(id = R.string.app_name))
                Text(
                    text = stringResource(id = R.string.last_upd) + ": " + currentDay.value.last_update,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        start = 8.dp
                    ),
                    style = TextStyle(fontSize = 15.sp)
                )
            }
        }
    )
//    Row(
//        modifier = Modifier.padding(16.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//
//        Text(
//            modifier = Modifier.clickable { /*TODO: dialog window with list of cities*/ },
//            text = stringResource(id = R.string.change_country),
//            color = Color.White
//        )
//    }
}

//@Preview(showBackground = true)
@Composable
fun ListItem(item: WeatherData, context: Context, bgColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    )
            ) {
                Text(
                    text = item.date,
                    color = Color.White
                )
            }
            AsyncImage(
                model = "http:" + item.icon,
                contentDescription = "list_temp",
                modifier = Modifier
                    .fillMaxHeight()
                    .size(45.dp)
            )
            Row(
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = item.min_temp + "º",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
                Text(
                    text = "–",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
                Text(
                    text = item.max_temp + "º",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun dopLayout(currentDay: MutableState<WeatherData>, bgColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Card(
            modifier = Modifier
                .width(200.dp)
                .padding(horizontal = 15.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.humidity))
                Text(
                    text = currentDay.value.humidity + "%",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
            }
        }
        Card(
            modifier = Modifier
                .width(200.dp)
                .padding(horizontal = 15.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.w_speed))
                Text(
                    text = currentDay.value.wind_speed + stringResource(id = R.string.m_sec),
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
            }

        }
    }
}


