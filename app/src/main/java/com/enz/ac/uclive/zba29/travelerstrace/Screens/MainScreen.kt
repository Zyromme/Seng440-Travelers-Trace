package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.service.TrackingService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel, onStart: (Long) -> Unit) {
    val journeyList by viewModel.journeys.observeAsState(listOf())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val dateFormatter = DateTimeFormatter.ofPattern(stringResource(id = R.string.date_pattern))
    val currentDate = LocalDate.now().format(dateFormatter)
    val scope = rememberCoroutineScope()

    val navBackStackEntry = navController.currentBackStackEntryAsState().value

    LaunchedEffect(navBackStackEntry) {
        // Listen for changes in the back stack entry
        navBackStackEntry?.destination?.route?.let { currentDestination ->
            if (currentDestination == Screen.MainScreen.route) {
                // Reload data when returning from OnJourneyScreen
                viewModel.reloadJourneyList()
            }
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer (
                drawerState = drawerState,
                navController = navController,
            )
        }) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text= stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = {scope.launch {drawerState.open()}}) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
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
                    journeyList.forEach {
                        JourneyCard(
                            it
                        )
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
                                type = ""
                            ))
                        navController.navigate(Screen.OnJourneyScreen.withArgs(id.toString()))
                        onStart(id)
                    } else {
                        navController.navigate(Screen.OnJourneyScreen.withArgs(viewModel.journeyId!!))
                    }
                }
            },
//                containerColor = Color.Green,
                    shape = CircleShape,
                ) {
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
}