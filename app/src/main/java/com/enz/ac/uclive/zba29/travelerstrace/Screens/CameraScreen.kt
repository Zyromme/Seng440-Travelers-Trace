package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.content.Context
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Autorenew
import androidx.compose.material.icons.sharp.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.CameraScreenViewModel
import com.enz.ac.uclive.zba29.travelerstrace.service.TrackingService
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

private fun switchCamera(lensFacing: Int): Int {
    return if (lensFacing == CameraSelector.LENS_FACING_BACK)
        CameraSelector.LENS_FACING_FRONT
    else
        CameraSelector.LENS_FACING_BACK
}

private fun takePhoto(
    imageCapture: ImageCapture,
    outputDirectory: File,
    cameraExecutor: Executor,
    handlePhotoCapture: (File) -> Unit
) {
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(outputOptions, cameraExecutor, object: ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
        }
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            handlePhotoCapture(photoFile)
        }
    })
}


@Composable
fun CameraScreen(
    navController: NavController,
    outputDirectory: File,
    cameraExecutor: Executor,
    cameraViewModel: CameraScreenViewModel,
    journeyId: String?
) {
    val lensFacing = cameraViewModel.lensFacing
    val scope = rememberCoroutineScope()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val dialogMaxHeight: Float

    // Camera preview setup
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(cameraViewModel.lensFacing)
        .build()

    fun handlePhotoCapture(newPhoto: File) {
        cameraViewModel.photoFile = newPhoto
    }

    fun deletePhotoCaptured() {
        if (cameraViewModel.photoFile?.exists() == true) {
            cameraViewModel.photoFile?.delete()
            cameraViewModel.photoFile = null
        }
    }

    fun savePhotoToDB() {
        if (cameraViewModel.photoFile != null) {
            if (journeyId != null) {
                val latLong = TrackingService.pathPoints.value!!.last()
                scope.launch {
                    cameraViewModel.savePhotoToRepo(journeyId, latLong)
                }
            }
        }
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }



    if (isLandscape) {
        dialogMaxHeight = 1.0f
        LandscapeCameraScreen(
            navController = navController,
            outputDirectory = outputDirectory,
            cameraExecutor = cameraExecutor,
            previewView = previewView,
            handlePhotoCapture = ::handlePhotoCapture,
            imageCapture = imageCapture,
            cameraViewModel = cameraViewModel,
            journeyId = journeyId
        )
    } else {
        dialogMaxHeight = 0.8f
        PortraitCameraScreen(
            navController = navController,
            outputDirectory = outputDirectory,
            cameraExecutor = cameraExecutor,
            previewView = previewView,
            handlePhotoCapture = ::handlePhotoCapture,
            imageCapture = imageCapture,
            cameraViewModel = cameraViewModel,
            journeyId = journeyId
        )
    }

    if (cameraViewModel.photoFile != null) {
        ConfirmPhotoDialog(
            Uri.fromFile(cameraViewModel.photoFile).toString(),
            navController,
            ::deletePhotoCaptured,
            dialogMaxHeight,
            ::savePhotoToDB,
            journeyId
        )
    }
}

@Composable
fun PortraitCameraScreen(
    navController: NavController,
    outputDirectory: File,
    cameraExecutor: Executor,
    previewView: PreviewView,
    handlePhotoCapture: (File) -> Unit,
    imageCapture: ImageCapture,
    cameraViewModel: CameraScreenViewModel,
    journeyId: String?
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.13f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.weight(0.3f),
                onClick = {navController.navigate(Screen.OnJourneyScreen.withArgs(journeyId!!))},
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text(text = stringResource(id = R.string.cancel_camera), color = Color.White)
            }
            IconButton(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxSize(0.8f),
                onClick = { takePhoto(imageCapture, outputDirectory, cameraExecutor, handlePhotoCapture) },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.Camera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            )
            IconButton(
                modifier = Modifier.weight(0.3f),
                onClick = { cameraViewModel.lensFacing = switchCamera(cameraViewModel.lensFacing) },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.Autorenew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            )
        }
    }
}


@Composable
fun LandscapeCameraScreen(
    navController: NavController,
    outputDirectory: File,
    cameraExecutor: Executor,
    previewView: PreviewView,
    handlePhotoCapture: (File) -> Unit,
    imageCapture: ImageCapture,
    cameraViewModel: CameraScreenViewModel,
    journeyId: String?
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxWidth(0.13f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                modifier = Modifier.weight(0.3f),
                onClick = {
                    cameraViewModel.lensFacing = switchCamera(cameraViewModel.lensFacing)
                },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.Autorenew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            )
            IconButton(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxSize(0.8f),
                onClick = { takePhoto(imageCapture, outputDirectory, cameraExecutor, handlePhotoCapture) },
                content = {
                    Icon(
                        imageVector = Icons.Sharp.Camera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            )
            Button(
                modifier = Modifier.weight(0.3f),
                onClick = { navController.navigate(Screen.OnJourneyScreen.withArgs(journeyId!!)) },
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text(text = stringResource(id = R.string.cancel_camera), color = Color.White)
            }
        }
    }
}



@Composable
fun ConfirmPhotoDialog(
    photoUri: String,
    navController: NavController,
    deletePhoto: () -> Unit,
    maxHeight: Float,
    savePhotoToDB: () -> Unit,
    journeyId: String?
) {
    val painter = rememberImagePainter(data = photoUri)
    Dialog(
        onDismissRequest = {navController.navigate(Screen.MainScreen.route)},
        properties = DialogProperties( dismissOnBackPress = false,dismissOnClickOutside = false)
    ) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(maxHeight)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = { deletePhoto() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(id = R.string.retake_camera))
                    }
                    Button(
                        onClick = {
                            savePhotoToDB()
                            navController.navigate(Screen.OnJourneyScreen.withArgs(journeyId!!)) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(id = R.string.save_camera))
                    }
                }
            }
        }
    }
}
