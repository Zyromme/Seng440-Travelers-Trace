package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.MapState
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val journeyRepository: JourneyRepository) : ViewModel() {
    var journeyId by mutableStateOf<String?>(null)
    var journeys: LiveData<List<Journey>> = journeyRepository.journeys.asLiveData()
    val numJourneys: LiveData<Int> = journeyRepository.numJourneys.asLiveData()
    suspend fun addJourney(journey: Journey): Long {
        return journeyRepository.insert(journey)
    }

    fun reloadJourneyList() {
        journeys = journeyRepository.journeys.asLiveData()
    }

    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null
        )
    )
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    state.value = state.value.copy(
                        lastKnownLocation = task.result,
                    )
                }
            }
        } catch (e: SecurityException) {
            // TODO show error?
        }
    }

}