package com.enz.ac.uclive.zba29.travelerstrace.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.enz.ac.uclive.zba29.travelerstrace.model.Settings
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference

class StoreSettings(context: Context) {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    var pref = context.dataStore

    companion object {
        private var instanceRef: WeakReference<StoreSettings>? = null

        fun getInstance(context: Context): StoreSettings {
            var instance = instanceRef?.get()
            if (instance == null) {
                instance = StoreSettings(context.applicationContext)
                instanceRef = WeakReference(instance)
            }
            return instance
        }
    }

    private object Keys {
        var isDark = booleanPreferencesKey("IS_DARK")
        var metric = stringPreferencesKey("METRIC")
        var language = stringPreferencesKey("LANGUAGE")
        var trackingInterval = stringPreferencesKey("TRACKING_INTERVAL")
    }

    suspend fun setSettings(settings: Settings) {
        pref.edit {
            it[Keys.isDark] = settings.isDark
            it[Keys.metric] = settings.metric
            it[Keys.language] = settings.language
            it[Keys.trackingInterval] = settings.trackingInterval
        }
    }

    fun getSettings() = pref.data.map {
        Settings(
            isDark = it[Keys.isDark]?: true,
            metric = it[Keys.metric]?: "Metric",
            language = it[Keys.language]?: "English",
            trackingInterval = it[Keys.trackingInterval]?: "5s"
        )
    }
}