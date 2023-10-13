package com.enz.ac.uclive.zba29.travelerstrace.Screens


import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.OnJourneyViewModel
import com.enz.ac.uclive.zba29.travelerstrace.datastore.StoreSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnJourneyScreen(journeyId: String?,
                    navController: NavController,
                    onJourneyViewModel: OnJourneyViewModel,
                    onStop: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.walking_animation))
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Log.e("", journeyId.toString())
    val settingsStore = StoreSettings.getInstance(LocalContext.current)
    var requestInterval = 0L
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet = remember { mutableStateOf(false) }

    fun saveJourneyDetails() {
        if (onJourneyViewModel.journeyTitle != null) {
            if (journeyId != null) {
                scope.launch {
                    onJourneyViewModel.updateJourney(journeyId.toLong())
                    onJourneyViewModel.journeyTitle = ""
                    onJourneyViewModel.description = ""
                }
            }
        }
    }

    LaunchedEffect(Unit){
        when(settingsStore.getSettings().first().trackingInterval) {
            "3s" -> requestInterval = 3000L
            "5s" -> requestInterval = 5000L
            "10s" -> requestInterval = 10000L
        }
    }

    LaunchedEffect(Unit) {
        while(true) {
            Log.e("Testing time", "new time" + journeyId)
            delay(requestInterval)
        }
    }

    editJourneyModal(
        onJourneyViewModel,
        onSave = {saveJourneyDetails()},
        sheetState = sheetState,
        showBottomSheet = showBottomSheet,
        scope = scope
    )

    if (isLandscape) {
        LandScapeOnJourneyScreen(
            navController = navController,
            composition = composition,
            onJourneyViewModel = onJourneyViewModel,
            onStop = onStop,
            journeyId = journeyId!!,
            saveJourneyDetails = { saveJourneyDetails() },
            showBottomSheet = showBottomSheet,
            scope = scope
            )
    } else {
        PortraitOnJourneyScreen(
            navController = navController,
            composition = composition,
            onJourneyViewModel = onJourneyViewModel,
            onStop = onStop,
            journeyId = journeyId!!,
            saveJourneyDetails = { saveJourneyDetails() },
            showBottomSheet = showBottomSheet,
            scope = scope
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitOnJourneyScreen(
    navController: NavController,
    composition: LottieComposition?,
    onJourneyViewModel: OnJourneyViewModel,
    onStop: () -> Unit,
    journeyId: String,
    saveJourneyDetails: () -> Unit,
    showBottomSheet: MutableState<Boolean>,
    scope: CoroutineScope,
    ) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.MainScreen.route) }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LottieAnimation(
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .weight(0.3f),
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.7f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {


                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = onJourneyViewModel.journeyTitle,
                        onValueChange = { newTitle ->
                            onJourneyViewModel.journeyTitle = newTitle
                        },
                        label = { Text(stringResource(id = R.string.on_journey_title)) }
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = onJourneyViewModel.description,
                        onValueChange = { newDescription ->
                            onJourneyViewModel.description = newDescription
                        },
                        minLines = 5,
                        maxLines = 5,
                        label = { Text(stringResource(id = R.string.on_journey_description)) }
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = { navController.navigate(Screen.CameraScreen.withArgs(journeyId)) },
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.CameraAlt,
                            contentDescription = "Button to go to camera",
                            tint = Color.White
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = {
                            onStop()
                            saveJourneyDetails()
                            onJourneyViewModel.saveAndMapLatLongToList(journeyId.toLong())
                            /*TODO: Change route to the journey detail screen*/
                            navController.navigate(Screen.MainScreen.route) },
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Text(stringResource(id = R.string.on_journey_end))
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandScapeOnJourneyScreen(
    navController: NavController,
    composition: LottieComposition?,
    onJourneyViewModel: OnJourneyViewModel,
    onStop: () -> Unit,
    journeyId: String,
    saveJourneyDetails: () -> Unit,
    showBottomSheet: MutableState<Boolean>,
    scope: CoroutineScope,
    ) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.MainScreen.route) }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                LottieAnimation(
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .weight(0.3f),
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.5f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = onJourneyViewModel.journeyTitle,
                        onValueChange = { newTitle ->
                            onJourneyViewModel.journeyTitle = newTitle
                        },
                        label = { Text(stringResource(id = R.string.on_journey_title)) }
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = onJourneyViewModel.description,
                        onValueChange = { newDescription ->
                            onJourneyViewModel.description = newDescription
                        },
                        minLines = 5,
                        maxLines = 5,
                        label = { Text(stringResource(id = R.string.on_journey_description)) }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = { navController.navigate(Screen.CameraScreen.withArgs(journeyId)) },
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.CameraAlt,
                            contentDescription = "Button to go to camera",
                            tint = Color.White
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        onClick = {
                            onStop()
                            onJourneyViewModel.saveAndMapLatLongToList(journeyId.toLong())
                            saveJourneyDetails()
                            navController.navigate(Screen.MainScreen.route) },
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Text(stringResource(id = R.string.on_journey_end))
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editJourneyModal(
    onJourneyViewModal: ViewModel,
    onSave: () -> Unit,
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    scope: CoroutineScope
) {

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Button(onClick = {

                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet.value = false
                    }
                }
            }) {
                Text("Hide bottom sheet")
            }
        }
    }
}