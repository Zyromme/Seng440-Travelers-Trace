package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import com.enz.ac.uclive.zba29.travelerstrace.repository.LatLongRepository
import com.enz.ac.uclive.zba29.travelerstrace.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val journeyRepository: JourneyRepository,
    private val latLongRepository: LatLongRepository,
    private val photoRepository: PhotoRepository
    ) : ViewModel() {
    var journeyId by mutableStateOf<String?>(null)
    var journeys: LiveData<List<Journey>> = journeyRepository.journeys.asLiveData()
    val numJourneys: LiveData<Int> = journeyRepository.numJourneys.asLiveData()
    suspend fun addJourney(journey: Journey): Long {
        return journeyRepository.insert(journey)
    }

    fun reloadJourneyList() {
        journeys = journeyRepository.journeys.asLiveData()
    }

    fun deleteJourney(journey: Journey) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            journeyRepository.deleteJourney(journey)
            latLongRepository.deleteAllLatLongByJourneyId(journey.id)
            photoRepository.deleteAllPhotosByJourneyId(journey.id)
        }
    }

    suspend fun getFirstPhoto(journeyId: Long): Photo {
        val photo = photoRepository.getAllPhotosByJourneyId(journeyId)
        return if (photo.isNotEmpty()) photo[0] else Photo(0,0,0.0,0.0,"")
    }

}