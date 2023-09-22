package com.enz.ac.uclive.zba29.travelerstrace


import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import com.enz.ac.uclive.zba29.travelerstrace.dat.FakeDatabase
import com.enz.ac.uclive.zba29.travelerstrace.ui.theme.TravelersTraceTheme
import java.util.concurrent.Executors
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.enz.ac.uclive.zba29.travelerstrace.Screens.*
import java.util.concurrent.Executor


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    private lateinit var cameraExecutor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        cameraExecutor = Executors.newSingleThreadExecutor()

        //TODO
        val isDark = mutableStateOf(false)

        fun toggleTheme(boolean: Boolean) {
            isDark.value = boolean
        }

        setContent {
            TravelersTraceTheme(
                darkTheme = isDark.value
            ) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
                    composable(route = Screen.MainScreen.route) {
                        MainScreen(navController = navController, FakeDatabase.journeyList)
                    }
                    composable(
                        route = Screen.MapScreen.route,
                    ) {
                        MapScreen(navController = navController)
                    }
                    composable(route = Screen.SettingsScreen.route) {
                        SettingsScreen(
                            navController = navController,
                            currentTheme = isDark.value,
                            onToggleTheme = { boolean -> toggleTheme(boolean) })
                    }
                    composable(
                        route = Screen.JourneyDetailScreen.route + "/{journeyId}",
                        arguments = listOf(
                            navArgument("journeyId") {
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) { entry ->
                        JourneyDetailScreen(
                            journeyId = entry.arguments?.getString("journeyId"),
                            navController = navController
                        )
                    }
                    composable(route = Screen.CameraScreen.route) {
                        CameraScreen(
                            navController = navController,
                            executor = cameraExecutor
                        )
                    }
                }
            }
        }
    }
}