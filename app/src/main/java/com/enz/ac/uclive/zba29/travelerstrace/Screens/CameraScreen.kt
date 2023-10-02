package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.content.Context
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Camera
import androidx.compose.material.icons.sharp.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.enz.ac.uclive.zba29.travelerstrace.ViewModel.CameraScreenViewModel
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
    handleImageCapture: (File) -> Unit
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, cameraExecutor, object: ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {}
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            handleImageCapture(photoFile)
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController, outputDirectory: File, cameraExecutor: Executor) {

    val cameraViewModel = viewModel<CameraScreenViewModel>()

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
            Log.i("TravelersTrace", "photo deleted")
            cameraViewModel.photoFile = null
        }
    }

    LaunchedEffect(cameraViewModel.lensFacing) {
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

    if (cameraViewModel.photoFile != null) {
        ConfirmPhotoDialog(Uri.fromFile(cameraViewModel.photoFile).toString(), navController, ::deletePhotoCaptured)
    }

    Scaffold (
        topBar = {
            TopAppBar (
                title = { Text("Settings") },
                navigationIcon = {
                    //TODO: Will have to change this to navigate back to on walk screen
                    IconButton(onClick = {navController.navigate(Screen.MainScreen.route)}) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.13f),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = {}) {}
                    IconButton(
                        modifier = Modifier
                            .fillMaxSize(0.8f),
                        onClick = { takePhoto(imageCapture, outputDirectory, cameraExecutor, ::handlePhotoCapture) },
                        content = {
                            Icon(
                                imageVector = Icons.Sharp.Camera,
                                contentDescription = "Take picture",
                                tint = Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    )
                    IconButton(
                        modifier = Modifier
                            .fillMaxSize(0.8f),
                        onClick = { cameraViewModel.lensFacing = switchCamera(cameraViewModel.lensFacing) },
                        content = {
                            Icon(
                                imageVector = Icons.Sharp.Cameraswitch,
                                contentDescription = "Take picture",
                                tint = Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun ConfirmPhotoDialog(
    photoUri: String,
    navController: NavController,
    deletePhoto: () -> Unit
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
                .fillMaxHeight(0.6f)
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
                    contentDescription = "Image",
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
                        Text("Retake")
                    }
                    Button(
                        onClick = {/*TODO: Route back to the OnJourneyScreen*/},
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
