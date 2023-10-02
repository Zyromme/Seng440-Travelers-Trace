package com.enz.ac.uclive.zba29.travelerstrace.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.enz.ac.uclive.zba29.travelerstrace.R


data class AppDrawerItemInfo(
    val route : String,
    @StringRes val title: Int,
    @DrawableRes val drawableId: Int,
)
object DrawerParams {
    val drawerButtons = listOf(
        AppDrawerItemInfo(
            "main_screen",
            R.string.drawer_home,
            R.drawable.baseline_home_24,
        ),
        AppDrawerItemInfo(
            "settings_screen",
            R.string.drawer_settings,
            R.drawable.baseline_settings_24,
        ),
        AppDrawerItemInfo(
            "map_screen",
            R.string.drawer_map,
            R.drawable.baseline_map_24,
        )
    )
}