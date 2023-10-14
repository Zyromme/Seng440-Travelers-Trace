package com.enz.ac.uclive.zba29.travelerstrace.repository

import androidx.annotation.WorkerThread
import com.enz.ac.uclive.zba29.travelerstrace.dao.JourneyDao
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class JourneyRepository @Inject constructor(private val journeyDao: JourneyDao) {
    val journeys: Flow<List<Journey>> = journeyDao.getAll()
    val numJourneys: Flow<Int> = journeyDao.getCount()

    @WorkerThread
    suspend fun insert(journey: Journey) : Long {
        return journeyDao.insert(journey)
    }

    @WorkerThread
    suspend fun updateJourney(journeyID: Long, title: String, description: String, totalDistance: Double?, duration: Int?) {
        journeyDao.updateJourney(journeyID, title, description, totalDistance, duration)
    }

    @WorkerThread
    suspend fun getJourneyById(journeyId: Long): Journey {
        return journeyDao.getJourneyById(journeyId)
    }

    @WorkerThread
    suspend fun deleteJourney(journey: Journey) {
        journeyDao.delete(journey)
    }
}