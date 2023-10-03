package com.enz.ac.uclive.zba29.travelerstrace

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.enz.ac.uclive.zba29.travelerstrace.Screens.*
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MainViewModel
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MapViewModel
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.OnJourneyViewModel
import com.enz.ac.uclive.zba29.travelerstrace.dat.FakeDatabase
import com.enz.ac.uclive.zba29.travelerstrace.datastore.StoreSettings
import com.enz.ac.uclive.zba29.travelerstrace.model.Settings
import com.enz.ac.uclive.zba29.travelerstrace.ui.theme.TravelersTraceTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                mapViewModel.getDeviceLocation(fusedLocationProviderClient)
            }
        }

    private fun askPermissions() = when {
        ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            mapViewModel.getDeviceLocation(fusedLocationProviderClient)
        }
        else -> {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val mapViewModel: MapViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val onJourneyViewModel: OnJourneyViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        askPermissions()

        setContent {
            val scope = rememberCoroutineScope()
            val settingsStore = StoreSettings.getInstance(LocalContext.current)
            var isDark by remember { mutableStateOf(false) }
            var settings by remember {
                mutableStateOf<Settings?>(Settings(isDark = true, metric = "km", language = "English"))
            }

            scope.launch {
                settings = settingsStore.getSettings().first()
                isDark = settings!!.isDark
            }

            fun updateSettings(newSettings: Settings) {
                scope.launch {
                    settingsStore.setSettings(newSettings)
                }
                isDark = newSettings.isDark
            }

            TravelersTraceTheme(
                darkTheme = isDark
            ) {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
                    composable(route = Screen.MainScreen.route) {
                        MainScreen(navController = navController, viewModel = mainViewModel)
                    }
                    composable(route = Screen.MapScreen.route,
                    ) {
                        MapScreen(navController = navController, state = mapViewModel.state.value)
                    }
                    composable(route = Screen.SettingsScreen.route) {
                        SettingsScreen(navController = navController, currentSettings = settings!!, onSettingsChange = {newSettings -> updateSettings(newSettings)})
                    }
                    composable(
                        route = Screen.JourneyDetailScreen.route + "/{journeyId}",
                        arguments = listOf(
                            navArgument("journeyId") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {
                            entry ->
                        JourneyDetailScreen(journeyId = entry.arguments?.getString("journeyId"), navController = navController)
                    }
                    composable(route = Screen.OnJourneyScreen.route + "/{journeyId}",
                        arguments = listOf(
                            navArgument("journeyId") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {
                            entry ->
                        OnJourneyScreen(journeyId = entry.arguments?.getString("journeyId"), navController = navController, onJourneyViewModel = onJourneyViewModel)
                    }
                }
            }
        }
    }
}