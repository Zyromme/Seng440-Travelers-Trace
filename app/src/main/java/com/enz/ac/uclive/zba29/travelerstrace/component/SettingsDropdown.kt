package com.enz.ac.uclive.zba29.travelerstrace.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdown(title: String,
                     choices: Array<String>,
                     initialValue: String,
                     onChange: (String) -> Unit) {
    var optionsExpanded by remember { mutableStateOf(false) }
    var optionSelected by remember { mutableStateOf(initialValue) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        ExposedDropdownMenuBox(
            modifier = Modifier,
            expanded = optionsExpanded,
            onExpandedChange = {
                optionsExpanded = !optionsExpanded
            }
        ) {
            TextField(
                value = optionSelected,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = optionsExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .width(150.dp), //TODO change this
                shape = RoundedCornerShape(15.dp)
            )

            ExposedDropdownMenu(
                modifier = Modifier.exposedDropdownSize(),
                expanded = optionsExpanded,
                onDismissRequest = { optionsExpanded = false }
            ) {
                choices.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            optionSelected = item
                            onChange(item)
                            optionsExpanded = false
                        }
                    )
                }
            }
        }
    }
}