package com.popovich.lemino

import android.app.Service
import android.content.Intent
import android.os.IBinder

const val mainNotificationId = 0
const val serviceChannelId = 1

class NetworkService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private lateinit var timer: LeminoTimer
    private lateinit var notifications: LeminoNotifications
    private lateinit var broadcasting: LeminoBroadcasting

    override fun onCreate() {
        super.onCreate()

        timer = LeminoTimer(applicationContext)
        notifications = LeminoNotifications(applicationContext)
        broadcasting = LeminoBroadcasting(applicationContext)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        notifications.createMainNotificationChannel()

        timer.startTimerTask()

        startForeground(serviceChannelId, notifications.buildForegroundServiceNotificationAndChannel())

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        broadcasting.broadcastRestartIntent()

        timer.stopTimerTask()
    }
}
