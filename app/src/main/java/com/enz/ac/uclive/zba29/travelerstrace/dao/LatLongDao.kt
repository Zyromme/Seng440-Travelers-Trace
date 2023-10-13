package com.enz.ac.uclive.zba29.travelerstrace.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.enz.ac.uclive.zba29.travelerstrace.model.LatLong
import kotlinx.coroutines.flow.Flow

@Dao
interface LatLongDao {
    @Insert
    suspend fun insert(latLong: LatLong): Long

    @Insert
    suspend fun insertAll(latLong: List<LatLong>)

    @Query("SELECT * FROM lat_long WHERE journeyId = :journeyId")
    fun getAllByJourneyId(journeyId: Long): Flow<List<LatLong>>
}