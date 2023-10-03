package com.enz.ac.uclive.zba29.travelerstrace.Screens

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object MapScreen : Screen("map_screen")
    object JourneyDetailScreen : Screen("journey_detail_screen")
    object SettingsScreen : Screen("settings_screen")
    object CameraScreen : Screen("camera_screen")
    object OnJourneyScreen : Screen("on_journey_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}