package com.enz.ac.uclive.zba29.travelerstrace.Screens


import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Description
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material.icons.sharp.Timer
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.JourneyDetailViewModel
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import com.enz.ac.uclive.zba29.travelerstrace.model.Settings
import com.enz.ac.uclive.zba29.travelerstrace.service.formatDistance
import com.enz.ac.uclive.zba29.travelerstrace.service.formatTime
import com.enz.ac.uclive.zba29.travelerstrace.ui.theme.md_theme_light_primary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun JourneyDetailScreen(
    journeyId: String?,
    navController: NavController,
    journeyDetailViewModel: JourneyDetailViewModel,
    settings: Settings,
    sharePhotosIntent: (List<Photo>) -> Unit,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var dialogMaxHeight = 0.8f
    if (isLandscape) {
        dialogMaxHeight = 1.0f
    }
    val scope = rememberCoroutineScope()
    var latLong by remember { mutableStateOf(journeyDetailViewModel.journeyGoogleLatLng) }
    var journey by remember { mutableStateOf(journeyDetailViewModel.currentJourney) }
    var photos by remember { mutableStateOf(journeyDetailViewModel.journeyPhotos) }
    var cameraPosition by remember { mutableStateOf(LatLng(-43.5320, 172.6306)) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet = remember { mutableStateOf(false) }
    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(journeyId) {
        journeyDetailViewModel.getJourneyById(journeyId!!.toLong())
        journeyDetailViewModel.getJourneyPhotos(journeyId.toLong())
        journeyDetailViewModel.getJourneyLatLongList(journeyId.toLong())
    }

    LaunchedEffect(
        journeyDetailViewModel.journeyPhotos,
        journeyDetailViewModel.journeyGoogleLatLng
    ) {
        latLong = journeyDetailViewModel.journeyGoogleLatLng
        journey = journeyDetailViewModel.currentJourney
        photos = journeyDetailViewModel.journeyPhotos
        cameraPosition = latLong[0]
    }

    LaunchedEffect(journeyId) {
        delay(1300)
        isLoading = false
    }

    var isPhotoDialogShowing by remember { mutableStateOf(false) }
    var currentDialogPhoto by remember { mutableStateOf("") }

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
        journey!!,
        settings.metric
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(journey!!.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.MainScreen.route) }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet.value = true }) {
                        Icon(
                            imageVector = Icons.Sharp.Info,
                            contentDescription = null
                        )
                    }
                    if (photos.isNotEmpty() && photos[0].filePath != "") {
                        IconButton(onClick = { sharePhotosIntent(photos) }) {
                            Icon(
                                imageVector = Icons.Sharp.Share,
                                contentDescription = null
                            )
                        }
                    }
                },
            )
        }
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .zIndex(1f)
                    .background(Color.Gray),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingAnimation()
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
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
                Marker(
                    state = MarkerState(LatLng(latLong.first().latitude, latLong.first().longitude)),
                    title = "Start"
                )

                Marker(
                    state = MarkerState(LatLng(latLong.last().latitude, latLong.last().longitude)),
                    title = "End"
                )

                photos.forEach { photo ->
                    PhotoMarkers(photo, { filePath -> showPhotoDialog(filePath) })
                }
                Polyline(
                    points = latLong,
                    color = Color.Blue,
                    width = 20f,
                )
            }
        }
    }

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
        PhotoImage(photoUri = photoUri)
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
    val painter = rememberAsyncImagePainter(model = File(photoUri))

    Image(
        painter = painter,
        contentDescription = null,
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
    journey: Journey,
    measureSetting: String
) {
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = buildString {
                            append(journey.date)
                        },
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(verticalAlignment = Alignment.Bottom) {

                        Icon(
                            imageVector = Icons.Sharp.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp, 16.dp),
                            tint = Color.Red
                        )

                        Text(
                            text = buildString {
                                append(formatDistance(journey.totalDistance, measureSetting))
                            },
                            modifier = Modifier.padding(8.dp, 12.dp, 12.dp, 0.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Icon(
                            imageVector = Icons.Sharp.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp, 16.dp),
                        )

                        Text(
                            text = buildString {
                                append(formatTime(journey.duration))
                            },
                            modifier = Modifier.padding(8.dp, 12.dp, 12.dp, 0.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(0.dp, 10.dp)
                    )
                    Row {
                        Icon(
                            imageVector = Icons.Sharp.Description,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp, 16.dp),
                        )
                        Text(
                            text = buildString {
                                append(stringResource(R.string.on_journey_description))
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = buildString {
                                append(journey.description)
                            },
                            modifier = Modifier.padding(8.dp, 12.dp, 12.dp, 0.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(50.dp))
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

@Composable
fun PhotoMarkers(photo: Photo, showPhotoDialog: (String) -> Unit) {
    if (photo.filePath != "") {
        val image = remember { mutableStateOf<BitmapDescriptor?>(null) }

        LaunchedEffect(Unit) {
            val bitmap = BitmapFactory.decodeFile(photo.filePath)
            val sizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
            val roundBitmap = getRoundedCornerBitmap(sizedBitmap)
            image.value = BitmapDescriptorFactory.fromBitmap(roundBitmap)
        }

        Marker(
            state = MarkerState(LatLng(photo.lat, photo.lng)),
            icon = image.value,
            onClick = {
                showPhotoDialog(photo.filePath)
                true
            }
        )
    }
}


/*
 Make a bitmap have rounded corners
https://stackoverflow.com/questions/74794828/custom-marker-icon-image-url-googlemap-compose-android
 */
fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
    val w = bitmap.width
    val h = bitmap.height
    val radius = (h / 2).coerceAtMost(w / 2)
    val output = Bitmap.createBitmap(w + 16, h + 16, Bitmap.Config.ARGB_8888)
    val paint = Paint()
    paint.isAntiAlias = true
    val canvas = Canvas(output)
    canvas.drawARGB(0, 0, 0, 0)
    paint.style = Paint.Style.FILL
    canvas.drawCircle(
        (w / 2 + 8).toFloat(),
        (h / 2 + 8).toFloat(),
        radius.toFloat(),
        paint
    ) //continue
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, 4f, 4f, paint)
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 10f
    canvas.drawCircle((w / 2 + 8).toFloat(), (h / 2 + 8).toFloat(), radius.toFloat(), paint)
    return output
}

/*
    Loading animation
    https://semicolonspace.com/jetpack-compose-loading-animation-2/
 */
@Composable
fun LoadingAnimation(
    circleColor: Color = md_theme_light_primary,
    animationDelay: Int = 1500
) {

    // 3 circles
    val circles = listOf(
        remember {
            Animatable(initialValue = 0f)
        },
        remember {
            Animatable(initialValue = 0f)
        },
        remember {
            Animatable(initialValue = 0f)
        }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            // Use coroutine delay to sync animations
            // divide the animation delay by number of circles
            delay(timeMillis = (animationDelay / 3L) * (index + 1))

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // outer circle
    Box(
        modifier = Modifier
            .size(size = 200.dp)
            .background(color = Color.Transparent)
    ) {
        // animating circles
        circles.forEachIndexed { index, animatable ->
            Box(
                modifier = Modifier
                    .scale(scale = animatable.value)
                    .size(size = 200.dp)
                    .clip(shape = CircleShape)
                    .background(
                        color = circleColor
                            .copy(alpha = (1 - animatable.value))
                    )
            ) {
            }
        }
    }
}