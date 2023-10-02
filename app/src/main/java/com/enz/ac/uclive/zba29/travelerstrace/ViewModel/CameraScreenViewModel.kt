package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.File

class CameraScreenViewModel: ViewModel() {
    var lensFacing by mutableIntStateOf(CameraSelector.LENS_FACING_BACK)
    var photoFile by mutableStateOf<File?>(null)
}
