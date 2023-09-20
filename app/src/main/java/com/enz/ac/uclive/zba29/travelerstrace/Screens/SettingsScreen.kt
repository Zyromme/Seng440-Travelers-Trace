package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.model.Settings

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentSettings: Settings,
    onSettingsChange: (Settings) -> Unit) {

    val distanceMetrics = arrayOf("km", "mi")
    val languages = arrayOf("English", "Pirate")

    var metricExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }

    val settings by remember { mutableStateOf(currentSettings) }

    Scaffold(
        topBar = {
            TopAppBar (
                title = { Text("Settings") },
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
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Distance Metric")
                    ExposedDropdownMenuBox(
                        modifier = Modifier,
                        expanded = metricExpanded,
                        onExpandedChange = {
                            metricExpanded = !metricExpanded
                        }
                    ) {
                        TextField(
                            value = settings.metric,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = metricExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .width(120.dp), //TODO change this
                            shape = RoundedCornerShape(15.dp)
                        )

                        ExposedDropdownMenu(
                            modifier = Modifier.exposedDropdownSize(),
                            expanded = metricExpanded,
                            onDismissRequest = { metricExpanded = false }
                        ) {
                            distanceMetrics.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        settings.metric = item
                                        onSettingsChange(settings)
                                        metricExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Language")
                    ExposedDropdownMenuBox(
                        modifier = Modifier,
                        expanded = languageExpanded,
                        onExpandedChange = {
                            languageExpanded = !languageExpanded
                        }
                    ) {
                        TextField(
                            value = settings.language,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = metricExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .width(150.dp), //TODO change this
                            shape = RoundedCornerShape(15.dp)
                        )

                        ExposedDropdownMenu(
                            modifier = Modifier.exposedDropdownSize(),
                            expanded = languageExpanded,
                            onDismissRequest = { languageExpanded = false }
                        ) {
                            languages.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(text = item) },
                                    onClick = {
                                        settings.language = item
                                        onSettingsChange(settings)
                                        languageExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

