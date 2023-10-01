package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val journeyRepository: JourneyRepository) : ViewModel() {
    val journeys: LiveData<List<Journey>> = journeyRepository.journeys.asLiveData()
    val numJourneys: LiveData<Int> = journeyRepository.numJourneys.asLiveData()

    fun addJourney(journey: Journey) = viewModelScope.launch {
        journeyRepository.insert(journey)
    }

    fun deleteJourney(journey: Journey) = viewModelScope.launch {
        journeyRepository.deleteJourney(journey)
        //TODO delete all LatLong and image linked to the journey
    }

}