package com.enz.ac.uclive.zba29.travelerstrace.service

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.enz.ac.uclive.zba29.travelerstrace.R
import com.enz.ac.uclive.zba29.travelerstrace.datastore.StoreSettings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


//typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<LatLng>
class TrackingService: LifecycleService() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var notificationManager : NotificationManager

    private var previousLocation: Location? = null

    private var totalDistance: Float = 0.0f

    var counter: Int = 0

    val stopwatchTimer = Timer()

    companion object {
        var isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val trackingInterval = MutableLiveData<Long>()
        val totalDistanceLiveData = MutableLiveData<Float>()
        var currentJourney = MutableLiveData<Long>()
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
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    applicationContext,
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
//            result.locations.let { locations ->
//                for(location in locations) {
//                    addPathPoint(location)
//                }
//            }
            addPathPoint(result.locations.last())
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value!!.add(pos)
            // Calculate distance from the previous location
            previousLocation?.let { previous ->
                val distance = location.distanceTo(previous)
                totalDistance += distance
            }

            // Update the previous location
            previousLocation = location

            Log.e("PREVIOUS", totalDistanceLiveData.toString())
            Log.e("CURRENT", totalDistance.toString())

            totalDistanceLiveData.postValue(totalDistance)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getNotificationManager()
        when(intent?.action) {
            Actions.START.toString() -> start(intent.getLongExtra("JOURNEY_ID", 0))
            Actions.STOP.toString() -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        isTracking.value = false
        currentJourney.value = 0
        stopwatchTimer.cancel()
        stopSelf()
    }

    private fun start(journeyId: Long) {
        currentJourney.postValue(journeyId)
        isTracking.postValue(true)
        startForeground(1, buildNotification())
        counter = 0
        stopwatchTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                counter++
                updateNotification()
            }
        }, 0, 1000)
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun updateNotification() {
        notificationManager.notify(
            1,
            buildNotification()
        )
    }

    private fun buildNotification(): Notification {

        val hours: Int = counter.div(60).div(60)
        val minutes: Int = counter.div(60)
        val seconds: Int = counter.rem(60)

//        val intent = Intent(this, MainActivity::class.java)
//        intent.action = Actions.SHOW_TRACKING.toString()
//        intent.putExtra("JOURNEY_ID", currentJourney.value)
//        val pIntent = PendingIntent.getActivity(this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, R.string.channel_id.toString())
            .setContentTitle("Tracing Run")
            .setOngoing(true)
            .setContentText(
                "${"%02d".format(hours)}:${"%02d".format(minutes)}:${
                    "%02d".format(
                        seconds
                    )
                }"
            )
            .setSmallIcon(R.mipmap.ic_launcher_travelers_trace_foreground)
            .setOnlyAlertOnce(true)
//            .setContentIntent(pIntent)
            .build()
    }

    enum class Actions {
        START, STOP, SHOW_TRACKING
    }
}