package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.LatLong
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import com.enz.ac.uclive.zba29.travelerstrace.repository.LatLongRepository
import com.enz.ac.uclive.zba29.travelerstrace.repository.PhotoRepository
import com.enz.ac.uclive.zba29.travelerstrace.service.TrackingService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyDetailViewModel @Inject constructor(private val journeyRepository: JourneyRepository, private val photoRepository: PhotoRepository, private val latLongRepository: LatLongRepository) : ViewModel() {

    var currentJourney by mutableStateOf<Journey?>(Journey(0, "", "", "",0.0, "", 0))
    var journeyPhotos by mutableStateOf(listOf(Photo(0,0,0.0,0.0,"")))
    var journeyLatLongList by mutableStateOf(listOf(LatLong(0,0,0.0, 0.0)))
    var journeyGoogleLatLng by mutableStateOf(listOf(LatLng(0.0, 0.0)))

    fun getJourneyById(journeyId: Long) = viewModelScope.launch {
        currentJourney = journeyRepository.getJourneyById(journeyId)
    }
    fun getJourneyPhotos(journeyId: Long) = viewModelScope.launch {
        journeyPhotos = photoRepository.getAllPhotosByJourneyId(journeyId)
    }

    fun getJourneyLatLongList(journeyId: Long) = viewModelScope.launch {
        journeyLatLongList = latLongRepository.getAllLatLongByJourneyId(journeyId)
        journeyGoogleLatLng = mapLatLongListToGoogleLatLong()
    }

    private fun mapLatLongListToGoogleLatLong(): List<LatLng> {
        val latLongList = journeyLatLongList.map { pathPoint ->
            LatLng(
                pathPoint.lat,
                pathPoint.lng
            )
        }
        return latLongList
    }

}