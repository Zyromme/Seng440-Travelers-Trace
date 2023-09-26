package com.enz.ac.uclive.zba29.travelerstrace.repository

import androidx.annotation.WorkerThread
import com.enz.ac.uclive.zba29.travelerstrace.dao.JourneyDao
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class JourneyRepository @Inject constructor(private val journeyDao: JourneyDao) {
    val journeys: Flow<List<Journey>> = journeyDao.getAll()
    val numJourneys: Flow<Int> = journeyDao.getCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(journey: Journey) {
        journeyDao.insert(journey)
    }
}