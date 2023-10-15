package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.twotone.Explore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.component.Drawer
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MainViewModel
import com.enz.ac.uclive.zba29.travelerstrace.component.JourneyCard
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.Settings
import com.enz.ac.uclive.zba29.travelerstrace.service.TrackingService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel, onStart: (Long) -> Unit, settings: Settings) {
    val journeyList by viewModel.journeys.observeAsState(listOf())
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var journeyToDelete by remember { mutableStateOf<Journey?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val dateFormatter = DateTimeFormatter.ofPattern(stringResource(id = R.string.date_pattern))
    val currentDate = LocalDate.now().format(dateFormatter)
    val scope = rememberCoroutineScope()
    var isDeleteConfirmationActive by remember { mutableStateOf(false) }

    fun showDeleteConfirmationDialog(journey: Journey) {
        journeyToDelete = journey
        isDeleteConfirmationActive = true
    }

    LaunchedEffect(isDeleteConfirmationActive) {
            viewModel.reloadJourneyList()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer (
                drawerState = drawerState,
                navController = navController,
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            content = {
                    LazyColumn(
                        modifier = Modifier.padding(it)
                    ) {
                        item(journeyList) {
                            journeyList.forEach { journey ->
                                if (journey.id != TrackingService.currentJourney.value) {
                                    val dismissState = rememberDismissState(
                                        positionalThreshold = { 130.dp.toPx() },
                                        confirmValueChange = { dismissValue ->
                                            if (dismissValue == DismissValue.DismissedToStart) {
                                                    showDeleteConfirmationDialog(journey)
                                            }
                                            true
                                        }
                                    )
                                    SwipeToDismiss(
                                        state = dismissState,
                                        directions = setOf(DismissDirection.EndToStart),
                                        background = {
                                            DismissBackground(dismissState = dismissState)
                                        },
                                        dismissContent = {
                                            JourneyCard(journey, navController, settings.metric, viewModel = viewModel)
                                        }
                                    )
                                }
                            }
                        }
                        item() {
                            Spacer(modifier = Modifier.padding(35.dp))
                        }
                    }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    scope.launch {
                        if (viewModel.journeyId == null) {
                            val id = viewModel.addJourney(
                                Journey(
                                    title = "",
                                    date = currentDate,
                                    totalDistance = 0.0,
                                    description = "",
                                    type = "",
                                    duration = 0
                                ))
                            navController.navigate(Screen.OnJourneyScreen.withArgs(id.toString()))
                            onStart(id)
                        } else {
                            navController.navigate(Screen.OnJourneyScreen.withArgs(viewModel.journeyId!!))
                        }
                    }
                },
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = if (viewModel.journeyId == null) Icons.Default.PlayArrow else Icons.TwoTone.Explore,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
        )

        if (isDeleteConfirmationActive) {
            DeleteConfirmationDialog(
                onConfirmDelete = {
                    viewModel.deleteJourney(journeyToDelete!!)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, R.string.toast_delete, Toast.LENGTH_SHORT).show()
                    isDeleteConfirmationActive = false // Reset delete confirmation state
                    journeyToDelete = null
                },
                onDismiss = {
                    isDeleteConfirmationActive = false // Reset delete confirmation state
                    journeyToDelete = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationDialog(
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(stringResource(id = R.string.delete_dialog_title)) },
        text = { Text(stringResource(id = R.string.delete_dialog_description)) },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete()
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.delete_dialog_delete))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.delete_dialog_cancel))
            }
        }
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
            contentDescription = stringResource(id = R.string.delete_dialog_delete)
        )
        Spacer(modifier = Modifier)
    }
}