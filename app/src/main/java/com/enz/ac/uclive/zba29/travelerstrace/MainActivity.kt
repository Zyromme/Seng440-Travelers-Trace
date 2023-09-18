package com.enz.ac.uclive.zba29.travelerstrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.enz.ac.uclive.zba29.travelerstrace.Screens.*
import com.enz.ac.uclive.zba29.travelerstrace.ui.theme.TravelersTraceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                        MainScreen(navController = navController)
                    }
                    composable(route = Screen.MapScreen.route,
                    ) {
                        MapScreen(navController = navController)
                    }
                    composable(route = Screen.SettingsScreen.route) {
                        SettingsScreen(navController = navController, currentTheme = isDark.value, onToggleTheme = {boolean -> toggleTheme(boolean)})
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
                }
            }
        }
    }
}