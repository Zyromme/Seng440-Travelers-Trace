package com.enz.ac.uclive.zba29.travelerstrace.repository

import androidx.annotation.WorkerThread
import com.enz.ac.uclive.zba29.travelerstrace.dao.LatLongDao
import com.enz.ac.uclive.zba29.travelerstrace.model.LatLong
import javax.inject.Inject


class LatLongRepository @Inject constructor(private val latLongDao: LatLongDao) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(latLong: LatLong) : Long {
        return latLongDao.insert(latLong)
    }
}