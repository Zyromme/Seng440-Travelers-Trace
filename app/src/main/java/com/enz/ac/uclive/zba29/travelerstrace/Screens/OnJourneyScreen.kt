package com.enz.ac.uclive.zba29.travelerstrace.Screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.ui.theme.TravelersTraceTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnJourneyScreen() {
    var journeyTitle by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.walking_animation))

    Scaffold (
        topBar = {
            TopAppBar (
                title = { Text("Traveler's Trace") },
                navigationIcon = {
                    IconButton(onClick = {/*navController.navigate(Screen.MainScreen.route)*/}) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LottieAnimation(
                    modifier = Modifier.size(300.dp),
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = journeyTitle,
                        onValueChange = { newTitle ->
                            journeyTitle = newTitle
                        },
                        label = { Text(text = "Journey Title") }
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = description,
                        onValueChange = { newDescription ->
                            description = newDescription
                        },
                        minLines = 5,
                        maxLines = 5,
                        label = { Text(text = "Tell me...") }
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = { /*TODO*/ },
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.CameraAlt,
                            contentDescription = "Button to go to camera",
                            tint = Color.White
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = { /*TODO*/ },
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Text(text = "End Journey")
                    }
                }
            }
        }
    )
}



@Preview(showBackground = true)
@Composable
fun OnJourneyScreenPreview() {
    TravelersTraceTheme(darkTheme = false) {
        OnJourneyScreen()
    }
}