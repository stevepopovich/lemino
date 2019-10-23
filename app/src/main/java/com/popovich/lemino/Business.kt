package com.popovich.lemino

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.EditText
import androidx.core.app.NotificationCompat

const val mainNotificationId = 0
const val serviceChannelId = 1

class Business {
    private lateinit var timer: MainTimer
    private val notifications = Notifications()
    private lateinit var broadcasting: Broadcasting

    fun startMainServiceBusinessLogic(context: Context, intent: Intent) {
        createMainNotificationChannel(context)

        timer = MainTimer(context)

        timer.startTimerTask(intent.getDoubleExtra(context.getString(R.string.threshold_key), defaultThresholdInBytes))
    }

    fun createMainNotificationChannel(context: Context) {
        notifications.createNotificationChannel(context, R.string.channel_id, R.string.channel_name)
    }


    fun startMainService(context: Context, mainActivity: MainActivity) {
        val threshold: EditText = mainActivity.findViewById(R.id.threshold)

        val serviceIntent = Intent(context, MainService::class.java)
        serviceIntent.putExtra(context.getString(R.string.threshold_key), threshold.text.toString().toDouble())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    fun stopMainServiceAndCancelNotification(context: Context) {
        context.stopService(Intent(context, MainService::class.java))

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(mainNotificationId)
    }

    fun buildForegroundServiceNotificationAndChannel(context: Context): Notification? {
        if (Build.VERSION.SDK_INT >= 26) {
            notifications.createNotificationChannel(context, R.string.service_channel_id, R.string.service_channel_description)

            return NotificationCompat.Builder(context, context.getString(R.string.service_channel_id))
                .setContentTitle("")
                .setContentText("").build()
        }

        return null
    }
}