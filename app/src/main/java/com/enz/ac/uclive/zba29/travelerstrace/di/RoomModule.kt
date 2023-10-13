package com.enz.ac.uclive.zba29.travelerstrace.di

import android.content.Context
import androidx.room.Room
import com.enz.ac.uclive.zba29.travelerstrace.dao.JourneyDao
import com.enz.ac.uclive.zba29.travelerstrace.dao.PhotoDao
import com.enz.ac.uclive.zba29.travelerstrace.dao.LatLongDao
import com.enz.ac.uclive.zba29.travelerstrace.database.JourneyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    fun provideJourneyDatabase(@ApplicationContext context: Context): JourneyDatabase{
        return Room.databaseBuilder(
            context,
            JourneyDatabase::class.java,
            "journey_database"
        ).build()
    }

    @Provides
    fun provideJourneyDao(journeyDatabase: JourneyDatabase): JourneyDao{
        return journeyDatabase.journeyDao()
    }

    @Provides
    fun providePhotoDao(journeyDatabase: JourneyDatabase): PhotoDao {
        return journeyDatabase.photoDao()
    }
    @Provides
    fun provideLatLongDao(journeyDatabase: JourneyDatabase): LatLongDao{
        return journeyDatabase.latLongDao()
    }
}