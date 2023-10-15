package com.enz.ac.uclive.zba29.travelerstrace.repository

import androidx.annotation.WorkerThread
import com.enz.ac.uclive.zba29.travelerstrace.dao.PhotoDao
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val photoDao: PhotoDao) {
    @WorkerThread
    suspend fun insert(photo: Photo) {
        photoDao.insert(photo)
    }

    @WorkerThread
    suspend fun getAllPhotosByJourneyId(journeyId: Long): List<Photo> {
        return photoDao.getAllPhotosByJourneyId(journeyId)
    }

    @WorkerThread
    suspend fun deleteAllPhotosByJourneyId(journeyId: Long) {
        photoDao.deleteAllPhotosByJourneyId(journeyId)
    }
}