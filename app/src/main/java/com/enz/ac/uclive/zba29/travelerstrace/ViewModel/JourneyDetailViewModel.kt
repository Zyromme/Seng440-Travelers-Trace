package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyDetailViewModel @Inject constructor(private val journeyRepository: JourneyRepository) : ViewModel() {

    var currentJourney by mutableStateOf<Journey?>(null)
    fun getJourneyById(journeyId: Long) = viewModelScope.launch {
        currentJourney = journeyRepository.getJourneyById(journeyId)
    }
}