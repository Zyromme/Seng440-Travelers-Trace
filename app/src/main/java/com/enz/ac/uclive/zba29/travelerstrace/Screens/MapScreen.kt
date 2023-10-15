package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel,
    fusedLocationProviderClient: FusedLocationProviderClient
) {
    val state = viewModel.state.value
    val mapProperties = MapProperties(
        isMyLocationEnabled = state.lastKnownLocation != null,
    )

    var lastKnownPosition = if (state.lastKnownLocation != null) LatLng(
        state.lastKnownLocation.latitude,
        state.lastKnownLocation.longitude
    ) else LatLng(0.0, 0.0)

    LaunchedEffect(state) {
        if (state.lastKnownLocation == null) {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
        } else {
            lastKnownPosition =
                LatLng(state.lastKnownLocation.latitude, state.lastKnownLocation.longitude)
        }
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lastKnownPosition, 20f)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.MainScreen.route) }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = mapProperties,
                    cameraPositionState = cameraPositionState
                ) {
                    val scope = rememberCoroutineScope()
                    MapEffect(state.lastKnownLocation) { map ->
                        map.setOnMapLoadedCallback {
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLng(
                                        lastKnownPosition
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
