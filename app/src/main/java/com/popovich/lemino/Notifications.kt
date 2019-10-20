package com.popovich.lemino

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notifications(val context: Context) {
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val broadcasting = Broadcasting(context)

    fun buildForegroundServiceNotificationAndChannel(): Notification? {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                context.getString(R.string.service_channel_id),
                context.getString(R.string.service_channel_description),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)

            return NotificationCompat.Builder(context, context.getString(R.string.service_channel_id))
                .setContentTitle("")
                .setContentText("").build()
        }

        return null
    }

    fun createMainNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(context.getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMainNotification() {
        val builder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_stat_onesignal_default)
            .setContentTitle(context.getString(R.string.main_notification_title))
            .setContentText(context.getString(R.string.main_notification_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_stat_onesignal_default, context.getString(R.string.STOP_LISTENING),
                broadcasting.getKillServicePendingInent())

        with(NotificationManagerCompat.from(context)) {
            notify(mainNotificationId, builder.build())
        }
    }
}

