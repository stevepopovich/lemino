package com.popovich.lemino

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notifications {
    // private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//    private val broadcasting = Broadcasting(context)
//

    fun showNotification(context: Context,
                         channelId: Int,
                         channelName: Int,
                         notificationId: Int,
                         smallIcon: Int,
                         title: Int,
                         content: Int,
                         action: NotificationCompat.Action?,
                         priority: Int?,
                         visibility: Int?,
                         setOnlyAlertOnce: Boolean?) {

        createNotificationChannel(context, channelId, channelName)

        val builder = NotificationCompat.Builder(context, context.getString(channelId))
            .setSmallIcon(smallIcon)
            .setContentTitle(context.getString(title))
            .setContentText(context.getString(content))//String.format("%.2f", megaBytesUsed)))
            .setPriority(priority ?: NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(visibility ?: NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(setOnlyAlertOnce ?: false)
            .addAction(action) //R.drawable.ic_stat_onesignal_default, context.getString(R.string.STOP_LISTENING), broadcasting.getKillServicePendingIntent()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

     fun createNotificationChannel(context: Context, channelId: Int, channelName: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(channelName)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(context.getString(channelId), name, importance)

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
//
//    fun showMainNotification(megaBytesUsed: Double) {
//        val builder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
//            .setSmallIcon(R.drawable.ic_stat_onesignal_default)
//            .setContentTitle(context.getString(R.string.main_notification_title))
//            .setContentText(context.getString(R.string.main_notification_content, String.format("%.2f", megaBytesUsed)))
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setOnlyAlertOnce(true)
//            .addAction(R.drawable.ic_stat_onesignal_default, context.getString(R.string.STOP_LISTENING),
//                broadcasting.getKillServicePendingIntent())
//
//        with(NotificationManagerCompat.from(context)) {
//            notify(mainNotificationId, builder.build())
//        }
//    }
}

