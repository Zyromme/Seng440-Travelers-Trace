package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MainViewModel
import com.enz.ac.uclive.zba29.travelerstrace.component.JourneyCard
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.enz.ac.uclive.zba29.travelerstrace.dat.FakeDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel) {
    val journeyList by viewModel.journeys.observeAsState(listOf())
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

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
                    journeyList.forEach { journey ->
                        val dismissState = rememberDismissState(
                            positionalThreshold = { 130.dp.toPx()},
                            confirmValueChange = { dismissValue ->
                                when (dismissValue) {
                                    DismissValue.DismissedToStart -> {
                                        viewModel.deleteJourney(journey)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        Toast.makeText(context, "Journey Removed", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {}
                                }
                                true
                            }
                        )
                        SwipeToDismiss(state = dismissState, directions = setOf(DismissDirection.EndToStart),
                            background = {
                                DismissBackground(dismissState = dismissState)
                            }
                            , dismissContent = {
                                JourneyCard(
                                    journey
                                )
                            } )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
//                navController.navigate(Screen.MapScreen.route)
                                           viewModel.addJourney(FakeDatabase.journeyList[0])
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.EndToStart -> Color(0xFFFF1744)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            modifier = Modifier.size(35.dp),
            imageVector = Icons.Default.Delete,
            contentDescription = "delete"
        )
        Spacer(modifier = Modifier)
    }
}