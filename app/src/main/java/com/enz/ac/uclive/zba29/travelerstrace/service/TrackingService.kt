package com.enz.ac.uclive.zba29.travelerstrace.service

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.enz.ac.uclive.zba29.travelerstrace.MainActivity
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.datastore.StoreSettings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.launch


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>
class TrackingService: LifecycleService() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val trackingInterval = MutableLiveData<Long>()
    }



    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })

        lifecycleScope.launch {
            val settings = StoreSettings.getInstance(applicationContext).getSettings().first()
            when(settings.trackingInterval) {
                "3s" -> trackingInterval.value = 3000L
                "5s" -> trackingInterval.value = 5000L
                "10s" -> trackingInterval.value = 10000L
            }
        }
    }

    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            val locationRequest =
                trackingInterval.value?.let {
                    LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, it)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(2000)
                        .build()
                }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            if (locationRequest != null) {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.let { locations ->
                for(location in locations) {
                    addPathPoint(location)
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> {
                pathPoints
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, R.string.channel_id.toString())
            .setSmallIcon(R.mipmap.ic_launcher_travelers_trace_foreground)
            .setContentTitle("Tracing Run")
            .setContentText("Elapsed time: 00:00")
            .setOngoing(true)
//            .setContentIntent(getCurrentTravelIntent())
            .build()
        startForeground(1, notification)
    }

    private fun getCurrentTravelIntent() =
        PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = Actions.SHOW_TRACKING.toString()
        },
            FLAG_UPDATE_CURRENT or FLAG_MUTABLE
    )

    enum class Actions {
        START, STOP, SHOW_TRACKING
    }
}