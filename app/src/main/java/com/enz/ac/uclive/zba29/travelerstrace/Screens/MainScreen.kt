package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.component.JourneyCard
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController, journeyList: List<Journey>) {
    Scaffold(
        topBar = {
            TopAppBar (
                title = { Text("Traveler's Trace") },
                actions = {
                    IconButton(onClick = {navController.navigate(Screen.SettingsScreen.route)}) {
                        Icon(Icons.Default.Settings, null)
                    }
                }
            )
        },
        content = {
            LazyColumn (
                modifier = Modifier.padding(it)
            ){
                item(journeyList) {
                    journeyList.forEach {
                        JourneyCard(
                            it
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.MapScreen.route)
            },
//                containerColor = Color.Green,
                shape = CircleShape,) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    )
}