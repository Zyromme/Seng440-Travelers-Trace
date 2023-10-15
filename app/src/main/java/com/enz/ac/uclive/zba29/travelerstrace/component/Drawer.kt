package com.enz.ac.uclive.zba29.travelerstrace.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.DirectionsWalk
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.model.DrawerParams
import kotlinx.coroutines.launch


@Composable
fun Drawer(
    drawerState: DrawerState,
    navController: NavController,
) {
    val menuItems = DrawerParams.drawerButtons
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable {
         mutableIntStateOf(0)
    }

    ModalDrawerSheet {
        Surface(
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Icon(
                    imageVector = Icons.Sharp.DirectionsWalk,
                    contentDescription = null,
                    tint = Color.White,
                )
                Text(
                    text = stringResource(id = R.string.app_name,),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.slogan),
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
        Divider(thickness = 3.dp)
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
                        imageVector = drawerItem.icon,
                        contentDescription = stringResource(id = drawerItem.title)
                    )
                }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
    }
}