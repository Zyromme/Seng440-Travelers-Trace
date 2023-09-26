package com.enz.ac.uclive.zba29.travelerstrace.Screens

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Camera
import androidx.compose.material.icons.sharp.Cameraswitch
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController) {

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

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
                        onClick = { navController.navigate(Screen.MainScreen.route) },
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
                        onClick = { lensFacing = switchCamera(lensFacing) },
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