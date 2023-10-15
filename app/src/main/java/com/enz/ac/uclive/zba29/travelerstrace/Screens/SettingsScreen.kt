package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.component.SettingsDropdown
import com.enz.ac.uclive.zba29.travelerstrace.model.Settings

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentSettings: Settings,
    onSettingsChange: (Settings) -> Unit) {

    val distanceMetrics = arrayOf(stringResource(R.string.metric), stringResource(R.string.imperial))
    val languages = arrayOf("English", "Pirate")
    val trackingInterval = arrayOf(stringResource(R.string.three_seconds), stringResource(R.string.five_seconds), stringResource(R.string.ten_seconds))

    val settings by remember { mutableStateOf(currentSettings) }

    Scaffold(
        topBar = {
            TopAppBar (
                title = {Text(text = stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(Screen.MainScreen.route)}) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        content = {
            Column( modifier = Modifier
                .fillMaxWidth()
                .padding(it)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Dark Mode")
                    Switch(
                        checked = settings.isDark,
                        onCheckedChange = {
                            settings.isDark = it
                            onSettingsChange(settings)
                        }
                    )
                }
                SettingsDropdown(
                    title = "Distance Metric",
                    choices = distanceMetrics,
                    initialValue = settings.metric,
                    onChange = { newMetric ->
                        settings.metric = newMetric
                        onSettingsChange(settings)
                    }
                )
                SettingsDropdown(
                    title = "Tracking Interval",
                    choices = trackingInterval,
                    initialValue = settings.trackingInterval,
                    onChange = { newTrackingInterval ->
                        settings.trackingInterval = newTrackingInterval
                        onSettingsChange(settings)
                    }
                )
            }
        }
    )
}

