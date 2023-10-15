package com.enz.ac.uclive.zba29.travelerstrace.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Insert
    suspend fun insert(photo: Photo): Long

    @Query("SELECT * FROM photos WHERE journeyId = :journeyId")
    suspend fun getAllPhotosByJourneyId(journeyId: Long): List<Photo>

    @Query("DELETE FROM photos WHERE journeyId = :journeyId")
    suspend fun deleteAllPhotosByJourneyId(journeyId: Long)
}