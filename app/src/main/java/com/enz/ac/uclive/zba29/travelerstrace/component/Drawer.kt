package com.enz.ac.uclive.zba29.travelerstrace.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.model.AppDrawerItemInfo
import kotlinx.coroutines.launch


@Composable
fun Drawer(
    drawerState: DrawerState,
    menuItems: List<AppDrawerItemInfo>,
    navController: NavController,
) {
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable {
         mutableIntStateOf(0)
    }

    ModalDrawerSheet {
        // Not sure what to place up here //
        Spacer(modifier = Modifier.height(12.dp))
            // generates on demand the required composables
            menuItems.forEachIndexed { index, drawerItem ->
                NavigationDrawerItem(
                    label = {
                    Text(text = stringResource(id = drawerItem.title))
                            },
                    selected = index == selectedItemIndex,
                    onClick = {
                        if (index != selectedItemIndex) {
                            navController.navigate(drawerItem.route)
                        }
                        selectedItemIndex = index
                        scope.launch {
                            drawerState.close()
                        }
                              },
                    icon = {
                    Icon(
                        painter = painterResource(id = drawerItem.drawableId),
                        contentDescription = stringResource(id = drawerItem.title)
                    )
                }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
    }
}

