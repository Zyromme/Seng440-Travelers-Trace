package com.enz.ac.uclive.zba29.travelerstrace.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.Screens.Screen


data class AppDrawerItemInfo<T>(
    val drawerOption: T,
    @StringRes val title: Int,
    @DrawableRes val drawableId: Int,
)
object DrawerParams {
    val drawerButtons = arrayListOf(
        AppDrawerItemInfo(
            DrawerNavOption.main_screen,
            R.string.drawer_home,
            R.drawable.baseline_home_24,
        ),
        AppDrawerItemInfo(
            DrawerNavOption.settings_screen,
            R.string.drawer_settings,
            R.drawable.baseline_settings_24,
        ),
        AppDrawerItemInfo(
            DrawerNavOption.map_screen,
            R.string.drawer_map,
            R.drawable.baseline_map_24,
        )
    )
}
enum class DrawerNavOption {
    main_screen,
    settings_screen,
    map_screen,
}