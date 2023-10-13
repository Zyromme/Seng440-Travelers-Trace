package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import com.enz.ac.uclive.zba29.travelerstrace.repository.PhotoRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(private val photoRepository: PhotoRepository): ViewModel() {
    var lensFacing by mutableIntStateOf(CameraSelector.LENS_FACING_FRONT)
    var photoFile by mutableStateOf<File?>(null)

    suspend fun savePhotoToRepo(journeyId: String, location: LatLng)
    {
        val photo = photoFile
        if (photo != null) {
            val newPhoto = Photo(
                journeyId = journeyId.toLong(),
                lat = location.latitude,
                lng = location.longitude,
                filePath = photo.path
            )
            photoRepository.insert(newPhoto)
            photoFile = null
        }
    }

}
