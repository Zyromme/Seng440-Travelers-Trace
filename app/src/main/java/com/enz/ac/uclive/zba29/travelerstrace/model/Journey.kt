package com.enz.ac.uclive.zba29.travelerstrace.model

import android.os.Parcel
import android.os.Parcelable
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
    ) : Parcelable {

    // Constructor for parcelable
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeDouble(totalDistance)
        parcel.writeString(type)
        parcel.writeInt(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Journey> {
        override fun createFromParcel(parcel: Parcel): Journey {
            return Journey(parcel)
        }

        override fun newArray(size: Int): Array<Journey?> {
            return arrayOfNulls(size)
        }
    }
}

