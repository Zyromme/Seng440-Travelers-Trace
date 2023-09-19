package com.enz.ac.uclive.zba29.travelerstrace.component


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun Drawer(
) {
    val items = listOf(Icons.Default.Settings)
    val selectedItem = remember { mutableStateOf(items[0]) }
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(12.dp))
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item, contentDescription = null) },
                label = { Text(text = item.name)},
                selected = item == selectedItem.value,
                onClick = { /*TODO*/ })
        }
    }
}
