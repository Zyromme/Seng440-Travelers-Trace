package com.enz.ac.uclive.zba29.travelerstrace.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journey")
class Journey(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo var title: String,
    @ColumnInfo var description: String,
    @ColumnInfo var date: String,
    @ColumnInfo var totalDistance: Double,
    @ColumnInfo var type: String,
    @ColumnInfo var duration: Int,
    )

