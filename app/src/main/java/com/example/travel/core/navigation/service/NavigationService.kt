package com.example.travel.core.navigation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NavigationService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_NAVIGATION) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val nextInstruction = intent?.getStringExtra(EXTRA_NEXT_INSTRUCTION) ?: "Navigating to destination..."
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🧭 Live Turn-by-Turn Navigation")
            .setContentText(nextInstruction)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        Timber.d("NavigationService started in foreground with instruction: $nextInstruction")

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Navigation Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "travel_buddy_navigation_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_NAVIGATION = "com.example.travel.STOP_NAVIGATION"
        const val EXTRA_NEXT_INSTRUCTION = "extra_next_instruction"
    }
}
