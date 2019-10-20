package com.popovich.lemino

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

const val mainNotificationId = 0
const val serviceChannelId = 1

class NetworkService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    var lastMobileReceived = 0L
    var lastMobileTransmitted = 0L // sent

    var timeNotificationPopped = 0L

    private val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var timer: Timer = Timer()
    private var timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            if (TrafficStats.getMobileTxBytes() - lastMobileTransmitted > 0 || TrafficStats.getMobileRxBytes() - lastMobileReceived > 0) {
                buildNotification()

                timeNotificationPopped = System.currentTimeMillis()
            }

            if (System.currentTimeMillis() - timeNotificationPopped > 1000) {
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.cancel(mainNotificationId)
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("lemino", "started network service")

        createNotificationChannel()

        lastMobileTransmitted = TrafficStats.getMobileTxBytes()
        lastMobileReceived = TrafficStats.getMobileRxBytes()

        startForegroundService()

        startTimer()

        return START_STICKY
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                getString(R.string.service_channel_id),
                getString(R.string.service_channel_description),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, getString(R.string.service_channel_id))
                .setContentTitle("")
                .setContentText("").build()

            startForeground(serviceChannelId, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        stopTimerTask()
    }

    private fun startTimer() {
        timer.schedule(timerTask, 0, 500)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification() {
        val broadcastIntent = Intent()
        broadcastIntent.action = "killservice"
        broadcastIntent.setClass(this, ServiceStopper::class.java)

        val killServicePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, broadcastIntent, 0)

        val builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_stat_onesignal_default)
            .setContentTitle(getString(R.string.main_notification_title))
            .setContentText(getString(R.string.main_notification_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_stat_onesignal_default, getString(R.string.STOP),
                killServicePendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(mainNotificationId, builder.build())
        }
    }

    private fun stopTimerTask() {
        timer.cancel()
    }
}
