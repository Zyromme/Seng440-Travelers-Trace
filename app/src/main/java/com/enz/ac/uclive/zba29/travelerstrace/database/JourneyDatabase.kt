package com.enz.ac.uclive.zba29.travelerstrace.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.enz.ac.uclive.zba29.travelerstrace.dao.JourneyDao
import com.enz.ac.uclive.zba29.travelerstrace.dao.LatLongDao
import com.enz.ac.uclive.zba29.travelerstrace.dao.PhotoDao
import com.enz.ac.uclive.zba29.travelerstrace.model.Journey
import com.enz.ac.uclive.zba29.travelerstrace.model.LatLong
import com.enz.ac.uclive.zba29.travelerstrace.model.Photo

@Database(entities = [Journey::class, LatLong::class, Photo::class], version = 1)
abstract class JourneyDatabase : RoomDatabase() {
    abstract fun journeyDao(): JourneyDao
    abstract fun latLongDao(): LatLongDao
    abstract fun photoDao(): PhotoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: JourneyDatabase? = null

        fun getDatabase(context: Context): JourneyDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JourneyDatabase::class.java,
                    "journey_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}