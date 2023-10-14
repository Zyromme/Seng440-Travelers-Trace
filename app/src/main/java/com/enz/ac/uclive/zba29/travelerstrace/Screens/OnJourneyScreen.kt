package com.enz.ac.uclive.zba29.travelerstrace.Screens


import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontStyle
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
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
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
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet = remember { mutableStateOf(false) }
//    val title = remember { mutableStateOf(onJourneyViewModel.journeyTitle + " " + journeyId) }
//    val description = remember { mutableStateOf(onJourneyViewModel.description) }
    val journey = remember {onJourneyViewModel.journey}


    LaunchedEffect(journeyId) {
        onJourneyViewModel.getJourneyById(journeyId!!.toLong())
        journey.value = onJourneyViewModel.journey.value
    }

    LaunchedEffect(onJourneyViewModel.journey) {
        journey.value = onJourneyViewModel.journey.value
    }
    fun saveJourneyDetails() {
        if (journeyId != null) {
            scope.launch {
                onJourneyViewModel.updateJourney(journeyId.toLong())
            }
        }
    }

    editJourneyModal(
        onJourneyViewModel,
        onSave = {saveJourneyDetails()},
        sheetState = sheetState,
        showBottomSheet = showBottomSheet,
        scope = scope,
//        title = title,
//        description = description
    )

    LaunchedEffect(onJourneyViewModel.journeyTitle, onJourneyViewModel.description) {

    }
    if (isLandscape) {
        LandScapeOnJourneyScreen(
            navController = navController,
            composition = composition,
            onJourneyViewModel = onJourneyViewModel,
            onStop = onStop,
            journeyId = journeyId!!,
            saveJourneyDetails = { saveJourneyDetails() },
            showBottomSheet = showBottomSheet,
            title = journey.value!!.title,
            description = journey.value!!.description
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
            title = journey.value!!.title,
            description = journey.value!!.description
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
    title: String,
    description: String
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            modifier = Modifier
                                .width(90.dp)
                                .height(20.dp)
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            onClick = {showBottomSheet.value = true},
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.edit_journey),
                                fontSize = 10.sp
                            )
                        }
                    }
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                        horizontalAlignment = Alignment.Start) {
                        Text(
                            text = stringResource(id = R.string.on_journey_title),
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(10.dp, 5.dp)
                        )
                        Text(
                            text = title
                            )
                        Text(
                            text = stringResource(id = R.string.on_journey_description),
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(10.dp, 5.dp)
                        )
                        Text(
                            text = description,
                            fontSize = 14.sp
                        )
                    }

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
    title: String,
    description: String
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
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                        horizontalAlignment = Alignment.Start) {
                        Text(
                            text = stringResource(id = R.string.on_journey_title),
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(10.dp, 5.dp)
                        )
                        Text(
                            text = title
                        )
                        Text(
                            text = stringResource(id = R.string.on_journey_description),
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(10.dp, 5.dp)
                        )
                        Text(
                            text = description,
                            fontSize = 14.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(20.dp)
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            onClick = {showBottomSheet.value = true},
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.edit_journey),
//                                fontSize = 10.sp
                            )
                        }
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
    onJourneyViewModel: OnJourneyViewModel,
    onSave: () -> Unit,
    sheetState: SheetState,
    showBottomSheet: MutableState<Boolean>,
    scope: CoroutineScope,
//    title: MutableState<String>,
//    description: MutableState<String>
) {

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(modifier = Modifier.padding(10.dp))
                Button(onClick = {
                    onSave()
                    showBottomSheet.value = false
                }) {
                    Text("Save Changes")
                }
                Spacer(modifier = Modifier.padding(50.dp))
            }

        }
    }
}