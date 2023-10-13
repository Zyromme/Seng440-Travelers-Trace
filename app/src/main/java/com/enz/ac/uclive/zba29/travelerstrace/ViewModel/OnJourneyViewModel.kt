package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.LatLong
import com.enz.ac.uclive.zba29.travelerstrace.repository.JourneyRepository
import com.enz.ac.uclive.zba29.travelerstrace.repository.LatLongRepository
import com.enz.ac.uclive.zba29.travelerstrace.service.TrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OnJourneyViewModel @Inject constructor(
    private val journeyRepository: JourneyRepository,
    private val latLongRepository: LatLongRepository
) : ViewModel() {
    var journeyTitle by mutableStateOf("")
    var description by mutableStateOf("")

    // Expose totalDistanceLiveData as a LiveData
    val totalDistanceLiveData: LiveData<Float>
        get() = TrackingService.totalDistanceLiveData
    suspend fun updateJourney(journeyId: Long) {
        val journeyTitle = journeyTitle
        val description = description
        journeyRepository.updateJourney(
                journeyId,
                journeyTitle.toString(),
                description.toString()
            )
    }


    fun saveAndMapLatLongToList(journeyId: Long) {
        val pathPoints = TrackingService.pathPoints.value
        if (pathPoints != null) {
            val latLongList = pathPoints.map { pathPoint ->
                LatLong(
                    journeyId = journeyId,
                    lat = pathPoint.latitude,
                    lng = pathPoint.longitude
                )
            }
            addAllLatLong(latLongList)
        }
    }

    fun addAllLatLong(latLongs: List<LatLong>) = viewModelScope.launch {
        latLongRepository.insertAll(latLongs)
    }
}