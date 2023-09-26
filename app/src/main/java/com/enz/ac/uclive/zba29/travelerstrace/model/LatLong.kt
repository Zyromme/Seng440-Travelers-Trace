package com.enz.ac.uclive.zba29.travelerstrace.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "lat_long")
class LatLong (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo var journeyId: Long,
    @ColumnInfo var lat: Double,
    @ColumnInfo var lng: Double,
    @ColumnInfo var dateTime: String,
    )