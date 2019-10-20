package com.popovich.lemino

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

class StoppedReason {
    companion object {
        var stoppedManually: Boolean = false
    }
}

class Restarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!StoppedReason.stoppedManually) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Toast.makeText(context, "Context restarting", Toast.LENGTH_SHORT).show()
                context.startForegroundService(Intent(context, NetworkService::class.java))
            } else {
                Toast.makeText(context, "Context restarting", Toast.LENGTH_SHORT).show()
                context.startService(Intent(context, NetworkService::class.java))
            }
        }
    }
}

class ServiceStopper: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        StoppedReason.stoppedManually = true

        context.stopService(Intent(context, NetworkService::class.java))

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(mainNotificationId)
    }
}