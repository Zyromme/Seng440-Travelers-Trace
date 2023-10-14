package com.enz.ac.uclive.zba29.travelerstrace.Screens



import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.JourneyDetailViewModel
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyDetailScreen(
    journeyId: String?,
    navController: NavController,
    journeyDetailViewModel: JourneyDetailViewModel
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var dialogMaxHeight = 0.8f
    if (isLandscape) {
        dialogMaxHeight = 1.0f
    }
    val scope = rememberCoroutineScope()
    var latLong by remember { mutableStateOf( journeyDetailViewModel.journeyGoogleLatLng ) }
    var journey by remember { mutableStateOf( journeyDetailViewModel.currentJourney ) }
    var photos by remember { mutableStateOf( journeyDetailViewModel.journeyPhotos ) }
    var cameraPosition by remember { mutableStateOf( LatLng(43.5320, 172.6306) ) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet = remember { mutableStateOf(false) }

    LaunchedEffect(journeyId) {
        journeyDetailViewModel.getJourneyById(journeyId!!.toLong())
        journeyDetailViewModel.getJourneyPhotos(journeyId.toLong())
        journeyDetailViewModel.getJourneyLatLongList(journeyId.toLong())
    }

    LaunchedEffect(journeyDetailViewModel.journeyPhotos, journeyDetailViewModel.journeyGoogleLatLng) {
        latLong = journeyDetailViewModel.journeyGoogleLatLng
        journey = journeyDetailViewModel.currentJourney
        photos = journeyDetailViewModel.journeyPhotos
        cameraPosition = latLong[0]
    }

    var isPhotoDialogShowing by remember{ mutableStateOf(false) }
    var currentDialogPhoto by remember{ mutableStateOf("") }

    fun showPhotoDialog(photoUri: String) {
        isPhotoDialogShowing = true
        currentDialogPhoto = photoUri
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraPosition, 20f)
    }
    val state = rememberScrollState()

    journeyDetails(
        sheetState,
        showBottomSheet,
        journey!!
    )

    Scaffold(
        topBar = {
            TopAppBar (
                title = { Text(journey!!.title) },
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(Screen.MainScreen.route)}) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet.value = true }) {
                        Icon(
                            imageVector = Icons.Sharp.Info,
                            contentDescription = "Journey Info Button"
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Sharp.Share,
                            contentDescription = "Share Journey Button"
                        )
                    }
                },
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ){
//                Column (
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(state)
//                        .padding(it)
//                ){
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        val scope = rememberCoroutineScope()
                        MapEffect(cameraPosition) { map ->
                            map.setOnMapLoadedCallback {
                                scope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLng(
                                            cameraPosition
                                        )
                                    )
                                }
                            }
                        }

                        photos.forEach { photo ->
                            Circle(
                                center = LatLng(photo.lat, photo.lng),
                                clickable = true,
                                fillColor = Color.Cyan.copy(alpha = 0.5f),
                                radius = 3.0,
                                strokeColor = Color.Blue,
                                onClick = {
                                    showPhotoDialog(photo.filePath)
                                }
                            )
                        }
                        Polyline(
                            points = latLong,
                            color = Color.Blue,
                            width = 20f,
                        )
                    }
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Column {
//                            Text(
//                                text = journey!!.title,
//                                fontSize = 30.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                            Text(
//                                text = journey!!.date,
//                                color = Color.LightGray,
//                                fontSize = 20.sp
//                            )
//                        }
//                        IconButton(
//                            onClick = { /*TODO: Call the share photo intent*/ },
//                            content = {
//                                Icon(
//                                    imageVector = Icons.Sharp.Share,
//                                    contentDescription = "Share Icon",
//                                    modifier = Modifier.fillMaxSize()
//                                )
//                            }
//
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Text(
//                        text = journey!!.description
//                    )
//                }
            }
        }
    )

    if (isPhotoDialogShowing) {
        DisplayPhotoDialog(
            photoUri = currentDialogPhoto,
            maxHeight = dialogMaxHeight,
            onDismissRequest = {
                isPhotoDialogShowing = false
                currentDialogPhoto = ""
            }
        )
    }
}



@Composable
fun DisplayPhotoDialog(
    photoUri: String,
    maxHeight: Float,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
                PhotoImage(photoUri =  photoUri)
    }
}

/*
 Gestures in Jetpack Compose
https://www.youtube.com/watch?v=1tkVjBxdGrk&t=1195s
https://gist.github.com/JolandaVerhoef/41bbacadead2ba3ce8014d67014efbdd
 */
@Composable
private fun PhotoImage(photoUri: String) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableStateOf(1f) }
    val painter = rememberImagePainter(data = File(photoUri))

    Image(
        painter = painter,
        contentDescription = "Photo",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        zoom = if (zoom > 1f) 1f else 2f
                        offset = calculateDoubleTapOffset(zoom, size, tapOffset)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, gestureZoom, _ ->
                    offset = offset.calculateNewOffset(
                        centroid, pan, zoom, gestureZoom, size
                    )
                    zoom = maxOf(1f, zoom * gestureZoom)
                }
            }
            .graphicsLayer {
                translationX = -offset.x * zoom
                translationY = -offset.y * zoom
                scaleX = zoom; scaleY = zoom
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .aspectRatio(1f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun journeyDetails(
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    journey: Journey
) {
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            // Sheet content

        }
    }
}

fun calculateDoubleTapOffset(
    zoom: Float,
    size: IntSize,
    tapOffset: Offset
): Offset {
    val newOffset = Offset(tapOffset.x, tapOffset.y)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}

fun Offset.calculateNewOffset(
    centroid: Offset,
    pan: Offset,
    zoom: Float,
    gestureZoom: Float,
    size: IntSize
): Offset {
    val newScale = maxOf(1f, zoom * gestureZoom)
    val newOffset = (this + centroid / zoom) -
            (centroid / newScale + pan / zoom)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}