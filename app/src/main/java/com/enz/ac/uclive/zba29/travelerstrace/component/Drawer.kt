package com.enz.ac.uclive.zba29.travelerstrace.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.enz.ac.uclive.zba29.travelerstrace.Screens.Screen
import com.enz.ac.uclive.zba29.travelerstrace.model.AppDrawerItemInfo
import kotlinx.coroutines.launch


@Composable
fun <T: Enum<T>> Drawer(
    drawerState: DrawerState,
    menuItems: List<AppDrawerItemInfo<T>>,
    defaultPick: T,
    onClick: (T) -> Unit
) {
    var currentPick by remember { mutableStateOf(defaultPick) }
    val coroutineScope = rememberCoroutineScope()

    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // generates on demand the required composables
            items(menuItems) { item ->
                // custom UI representation of the button
                AppDrawerItem(item = item) { navOption ->
                    if (currentPick == navOption) {
                        return@AppDrawerItem
                    }
                    currentPick = navOption
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    onClick(navOption)
                }
            }
        }
    }
}

@Composable
fun <T> AppDrawerItem(item: AppDrawerItemInfo<T>, onClick: (options: T) -> Unit) =
    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .width(150.dp),
        onClick = { onClick(item.drawerOption) },
        shape = RoundedCornerShape(50),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = item.drawableId),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }


