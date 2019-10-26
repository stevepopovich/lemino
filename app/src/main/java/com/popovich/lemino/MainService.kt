package com.popovich.lemino

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MainService : Service() {
    private val business = Business()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        business.startMainServiceBusinessLogic(applicationContext, intent)

        startForeground(R.integer.serviceChannelId, business.buildForegroundServiceNotificationAndChannel(applicationContext))

        return START_STICKY
    }
}
