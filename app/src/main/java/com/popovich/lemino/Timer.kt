package com.popovich.lemino

import android.app.NotificationManager
import android.content.Context
import android.net.TrafficStats
import java.util.*

class MainTimer constructor(private val context: Context) {
    private val notificationActiveTime = 500L// how long a notification will be alive
    private val timerPeriod = 250L // Time period in ms that we check data usage

    private var timeNotificationPopped = 0L

    private var lastMobileReceived = 0L
    private var lastMobileTransmitted = 0L // sent

    private val notifications = Notifications(context)

    private val timer: Timer = Timer()
    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            if (TrafficStats.getMobileTxBytes() - lastMobileTransmitted > 0 ||
                TrafficStats.getMobileRxBytes() - lastMobileReceived > 0) {
                notifications.showMainNotification()

                timeNotificationPopped = System.currentTimeMillis()
            }

            if (System.currentTimeMillis() - timeNotificationPopped > notificationActiveTime) {
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

        timer.schedule(timerTask, 0, timerPeriod)
    }
}