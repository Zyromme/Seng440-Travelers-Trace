package com.enz.ac.uclive.zba29.travelerstrace.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyDetailScreen(journeyId: String?, navController: NavController, mainViewModel: MainViewModel) {
    journeyId?.let { mainViewModel.getJourney(it.toLong()) }
    val journey = mainViewModel.currentJourney
    val journeyImagePainter = rememberImagePainter(data = journey?.image)

    Scaffold(
        topBar = {
            TopAppBar (
                title = { Text("Map") },
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(Screen.MainScreen.route)}) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ){
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item{
                        Column (
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                painter = journeyImagePainter,
                                contentDescription = "Journey Image",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    )
}