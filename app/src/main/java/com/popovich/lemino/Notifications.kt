package com.popovich.lemino

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notifications {
    fun showNotification(context: Context,
                         channelId: Int,
                         channelName: Int,
                         channelImportantance: Int,
                         notificationId: Int,
                         smallIcon: Int,
                         title: String,
                         content: String,
                         action: NotificationCompat.Action?,
                         priority: Int?,
                         visibility: Int?,
                         setOnlyAlertOnce: Boolean?) {

        createNotificationChannel(context, channelId, channelName, channelImportantance)

        val builder = NotificationCompat.Builder(context, context.getString(channelId))
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(priority ?: NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(visibility ?: NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(setOnlyAlertOnce ?: false)
            .addAction(action)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

     fun createNotificationChannel(context: Context, channelId: Int, channelName: Int, channelImportantance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(channelName)
            val channel = NotificationChannel(context.getString(channelId), name, channelImportantance)

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun cancelNotification(context: Context, channelId: Int) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(channelId)
    }
}

