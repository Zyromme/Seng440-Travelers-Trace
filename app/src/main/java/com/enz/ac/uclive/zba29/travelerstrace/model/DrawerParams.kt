package com.enz.ac.uclive.zba29.travelerstrace.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.Screens.Screen


data class AppDrawerItemInfo(
    val route : String,
    @StringRes val title: Int,
    val icon: ImageVector,
)
object DrawerParams {
    val drawerButtons = listOf(
        AppDrawerItemInfo(
            Screen.MainScreen.route,
            R.string.drawer_home,
            Icons.Default.Home,
        ),
        AppDrawerItemInfo(
            Screen.SettingsScreen.route,
            R.string.settings,
            Icons.Default.Settings,
        ),
        AppDrawerItemInfo(
            Screen.MapScreen.route,
            R.string.drawer_map,
            Icons.Default.Map,
        )
    )
}