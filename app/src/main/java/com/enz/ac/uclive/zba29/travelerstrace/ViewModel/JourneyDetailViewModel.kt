package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.lifecycle.ViewModel
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JourneyDetailViewModel @Inject constructor(private val journeyRepository: JourneyRepository) : ViewModel() {

    suspend fun getJourneyById(id: Long): Journey {
        return journeyRepository.getJourneyById(id)
    }
}