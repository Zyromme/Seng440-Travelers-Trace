package com.enz.ac.uclive.zba29.travelerstrace.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
class Photo (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo var journeyId: Long,
    @ColumnInfo var lat: Double,
    @ColumnInfo var lng: Double,
    @ColumnInfo var filePath: String
    )