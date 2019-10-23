package com.popovich.lemino

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder

class MainService : Service() {
    private val business = Business()

    //private val notifications = Notifications()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        business.startMainServiceBusinessLogic(applicationContext, intent)

        startForeground(serviceChannelId, business.buildForegroundServiceNotificationAndChannel(applicationContext))

        return START_STICKY
    }

    fun startForegroundService(channelId: Int, notification: Notification) {
        startForeground(channelId, notification)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//
//        // timer.stopTimerTask()
//    }
}
