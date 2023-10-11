package com.enz.ac.uclive.zba29.travelerstrace.service

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.enz.ac.uclive.zba29.travelerstrace.MainActivity
import com.enz.ac.uclive.zba29.travelerstrace.R

class TrackingService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
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