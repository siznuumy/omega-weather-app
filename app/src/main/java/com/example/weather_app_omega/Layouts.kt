package com.example.weather_app_omega

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.example.weather_app_omega.ui.theme.LightBlue

@Preview(showBackground = true)
@Composable
fun TESTmidLayout() {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "13:32",
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 8.dp
                        ),
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.White
                    )
                    AsyncImage(
                        model = "https:" + WeatherData().icon,
                        contentDescription = "im2",
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                end = 8.dp
                            )
                            .size(35.dp)
                    )
                }
                Text(
                    text = WeatherData().location,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
                Text(
                    text = WeatherData().temp + "ºC",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White
                )
                Text(
                    text = WeatherData().condition,
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )

                Text(
                    text = stringResource(id = R.string.min) + ": ${WeatherData().
                    max_temp}º " + stringResource(id = R.string.max)+ ": ${WeatherData()
                        .min_temp}º",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp))
                Text(
                    text = stringResource(id = R.string.humidity) + ": ${WeatherData().
                    humidity} " + stringResource(id = R.string.w_speed) + ": ${WeatherData()
                        .wind_speed}",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun topLayout(currentDay: MutableState<WeatherData>) {

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = currentDay.value.date,
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.size(4.dp))
        Row(modifier = Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically) {
//            ReusableImage(
//                image = if (isLightTheme) {
//                    R.drawable.ic_location_light
//                } else {
//                    R.drawable.ic_location_dark
//                },
//                contentScale = ContentScale.Fit,
//                contentDesc = "Location Icon",
//                modifier = Modifier
//                    .size(16.dp)
//                    .padding(end = 4.dp)
//            )
            Text(
                text = currentDay.value.location,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DaysLayout(){
    Box(modifier = Modifier
        .wrapContentSize()
        .background(Color.Blue)
        .clip(RoundedCornerShape(10.dp))) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            items(10) { ListItem() }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 4.dp),
//        elevation = CardElevation(10.dp),
        shape = RoundedCornerShape(15.dp),

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 5.dp,
                    bottom = 5.dp
                )
            ) {
                Text(
                    text = "11.07",
                    color = Color.White
                )
            }
            AsyncImage(
                model = "https://cdn.weatherapi.com/weather/64x64/day/116.png",
                contentDescription = "im5",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(100.dp)
            )
            Row(
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = "19º",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
                Text(
                    text = "–",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
                Text(
                    text = "25º",
                    color = Color.White,
                    style = TextStyle(fontSize = 25.sp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun infoLayout() {

}

