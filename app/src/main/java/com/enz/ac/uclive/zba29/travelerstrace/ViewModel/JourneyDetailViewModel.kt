package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import com.enz.ac.uclive.zba29.travelerstrace.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyDetailViewModel @Inject constructor(private val journeyRepository: JourneyRepository, private val photoRepository: PhotoRepository) : ViewModel() {

    var currentJourney by mutableStateOf<Journey?>(null)
    var journeyPhotos by mutableStateOf<Flow<List<Photo>>?>(null)
    fun getJourneyById(journeyId: Long) = viewModelScope.launch {
        currentJourney = journeyRepository.getJourneyById(journeyId)
    }

    fun getJourneyPhotos(journeyId: Long) = viewModelScope.launch {
        journeyPhotos = photoRepository.getAllPhotosByJourneyId(journeyId)
    }
}