package com.popovich.lemino

import android.app.NotificationManager
import android.content.Context
import android.net.TrafficStats
import java.util.*

class LeminoTimer constructor(val context: Context) {
    private var timeNotificationPopped = 0L

    private var lastMobileReceived = 0L
    private var lastMobileTransmitted = 0L // sent

    private val notifications = LeminoNotifications(context)

    private val timer: Timer = Timer()
    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            if (TrafficStats.getMobileTxBytes() - lastMobileTransmitted > 0 || TrafficStats.getMobileRxBytes() - lastMobileReceived > 0) {
                notifications.showMainNotification()

                timeNotificationPopped = System.currentTimeMillis()
            }

            if (System.currentTimeMillis() - timeNotificationPopped > 1000) {
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.cancel(mainNotificationId)
            }
        }
    }

    fun stopTimerTask() {
        timer.cancel()
    }

    fun startTimerTask() {
        lastMobileTransmitted = TrafficStats.getMobileTxBytes()
        lastMobileReceived = TrafficStats.getMobileRxBytes()

        timer.schedule(timerTask, 0, 500)
    }
}