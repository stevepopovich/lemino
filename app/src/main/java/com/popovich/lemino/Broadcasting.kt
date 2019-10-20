package com.popovich.lemino

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Broadcasting(private val context: Context) {
    fun getKillServicePendingIntent(): PendingIntent {
        val broadcastIntent = Intent()
        broadcastIntent.action = context.getString(R.string.kill_service_action)
        broadcastIntent.setClass(context, ServiceStopper::class.java)

        return PendingIntent.getBroadcast(context, 0, broadcastIntent, 0)
    }
}

class ServiceStopper: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.stopService(Intent(context, MainService::class.java))

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(mainNotificationId)
    }
}