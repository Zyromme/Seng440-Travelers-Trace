package com.enz.ac.uclive.zba29.travelerstrace.dao

import androidx.room.*
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Insert
    suspend fun insert(journey: Journey): Long

    @Update
    suspend fun update(journey: Journey)

    @Delete
    suspend fun delete(journey: Journey)

    @Query("SELECT * FROM journey")
    fun getAll(): Flow<List<Journey>>

    @Query("SELECT COUNT(*) FROM journey")
    fun getCount(): Flow<Int>

    @Query("SELECT * FROM journey WHERE id = :journeyId")
    suspend fun getJourneyById(journeyId: Long) : Journey
}