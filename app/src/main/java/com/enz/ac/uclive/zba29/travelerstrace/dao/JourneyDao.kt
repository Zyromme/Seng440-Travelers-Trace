package com.enz.ac.uclive.zba29.travelerstrace.dao

import androidx.room.*
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Insert
    suspend fun insert(journey: Journey): Long

    @Delete
    suspend fun delete(journey: Journey)

    @Query("SELECT * FROM journey")
    fun getAll(): Flow<List<Journey>>

    @Query("UPDATE journey SET title = :title, description = :description WHERE id = :journeyId;")
    suspend fun updateJourney(journeyId: Long, title: String, description: String)

    @Query("SELECT COUNT(*) FROM journey")
    fun getCount(): Flow<Int>
}