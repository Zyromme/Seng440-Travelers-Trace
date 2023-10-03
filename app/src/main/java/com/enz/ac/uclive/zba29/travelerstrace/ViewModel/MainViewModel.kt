package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.MapState
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val journeyRepository: JourneyRepository) : ViewModel() {
    val journeys: LiveData<List<Journey>> = journeyRepository.journeys.asLiveData()
    val numJourneys: LiveData<Int> = journeyRepository.numJourneys.asLiveData()
    suspend fun addJourney(journey: Journey): Long {
        return journeyRepository.insert(journey)
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