package com.enz.ac.uclive.zba29.travelerstrace.Screens



import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.JourneyDetailViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyDetailScreen(
    journeyId: String?,
    navController: NavController,
    journeyDetailViewModel: JourneyDetailViewModel,
    sharePhotoIntent: (File) -> Unit
) {
    var latLong by remember { mutableStateOf( journeyDetailViewModel.journeyGoogleLatLng ) }
    var journey by remember { mutableStateOf( journeyDetailViewModel.currentJourney ) }
    var photos by remember { mutableStateOf( journeyDetailViewModel.journeyPhotos ) }

    LaunchedEffect(journeyId) {
        journeyDetailViewModel.getJourneyById(journeyId!!.toLong())
        journeyDetailViewModel.getJourneyPhotos(journeyId.toLong())
        journeyDetailViewModel.getJourneyLatLongList(journeyId.toLong())
    }

    LaunchedEffect(journeyDetailViewModel.journeyPhotos, journeyDetailViewModel.journeyGoogleLatLng) {
        latLong = journeyDetailViewModel.journeyGoogleLatLng
        journey = journeyDetailViewModel.currentJourney
        photos = journeyDetailViewModel.journeyPhotos
    }


    val cameraPosition = latLong[0]
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 20f)
    }
    val state = rememberScrollState()

    Log.e("LAT_LONG", latLong.toString())

    Scaffold(
        topBar = {
            TopAppBar (
                title = { Text(journey!!.title) },
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(Screen.MainScreen.route)}) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state)
                        .padding(15.dp)
                ){
                    GoogleMap(
                        modifier = Modifier
                            .height(500.dp),
                        cameraPositionState = cameraPositionState
                    ) {
                        photos.forEach { photo ->
                            Marker(
                                state = MarkerState(LatLng(photo.lat, photo.lng))
                            )
                        }
                        Polyline(
                            points = latLong,
                            color = Color.Blue,
                            width = 20f,
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = journey!!.title,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = journey!!.date,
                                color = Color.LightGray,
                                fontSize = 20.sp
                            )
                        }
                        IconButton(
                            onClick = { /*TODO: Call the share photo intent*/ },
                            content = {
                                Icon(
                                    imageVector = Icons.Sharp.Share,
                                    contentDescription = "Share Icon",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = journey!!.description
                    )
                }
            }
        }
    )
}



//@Composable
//fun DisplayPhotoDialog(
//    photoUri: String
//) {
//    val painter = rememberImagePainter(data = photoUri)
//    Dialog(onDismissRequest = {}) {
//        Card {
//            Column {
//                Image(
//                    painter = painter,
//                    contentDescription = "Photo",
//                    contentScale = ContentScale.Fit,
//                    modifier
//                )
//            }
//        }
//    }
//}
