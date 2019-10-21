package com.popovich.lemino

import android.app.Service
import android.content.Intent
import android.os.IBinder

const val mainNotificationId = 0
const val serviceChannelId = 1

class MainService : Service() {
    private lateinit var timer: MainTimer
    private lateinit var notifications: Notifications
    private lateinit var broadcasting: Broadcasting

    override fun onCreate() {
        super.onCreate()

        timer = MainTimer(applicationContext)
        notifications = Notifications(applicationContext)
        broadcasting = Broadcasting(applicationContext)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        notifications.createMainNotificationChannel()

        timer.startTimerTask(intent.getDoubleExtra(getString(R.string.threshold_key), defaultThreshold))

        startForeground(serviceChannelId, notifications.buildForegroundServiceNotificationAndChannel())

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.stopTimerTask()
    }
}
