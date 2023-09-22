package com.enz.ac.uclive.zba29.travelerstrace.Screens

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.enz.ac.uclive.zba29.travelerstrace.component.CameraView
import java.io.File
import java.util.concurrent.Executor

fun captureImage(imageCapture: ImageCapture, cameraExecutor: Executor) {
    val file = File.createTempFile("img", ".jpg")
    val outputFileOption = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(outputFileOption, cameraExecutor, object : ImageCapture.OnImageSavedCallback{
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val uri = outputFileResults.savedUri
        }

        override fun onError(exception: ImageCaptureException) {
            TODO("Not yet implemented")
        }

    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navController: NavController, executor: Executor) {
    val imageCapture = remember {
        ImageCapture.Builder().build()
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
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    CameraView(imageCapture = imageCapture)
                    IconButton(
                        modifier = Modifier.padding(bottom = 20.dp),
                        onClick = {
                            captureImage(imageCapture, executor)
                            navController.navigate(Screen.MainScreen.route) },
                        content = {
                            Icon(
                                imageVector = Icons.Sharp.Lens,
                                contentDescription = "Take picture",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(1.dp)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                    )
                }
            }
        }
        )
}