package com.popovich.lemino

import android.app.NotificationManager
import android.content.Context
import android.net.TrafficStats
import java.util.*

class MainTimer constructor(private val context: Context) {
    private val notificationActiveTime = 1100L// how long a notification will be alive
    private val timerPeriod = 1000L // Time period in ms that we check data usage

    private var threshold = 0

    private var timeNotificationPopped = 0L

    private var lastMobileReceived = 0L
    private var lastMobileTransmitted = 0L // sent

    private val notifications = Notifications(context)

    private val timer: Timer = Timer()
    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            val bytesTransmitted = TrafficStats.getMobileTxBytes() - lastMobileTransmitted
            val bytesReceived = TrafficStats.getMobileRxBytes() - lastMobileReceived

            if (bytesTransmitted > threshold || bytesReceived > threshold) {
                val totalBytesUsed = (bytesReceived + bytesTransmitted).toDouble()
                val totalMegabytesUsed = totalBytesUsed / 1000000 // MB CONVERSION

                notifications.showMainNotification(totalMegabytesUsed)

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

    fun startTimerTask(threshold: Int) {
        this.threshold = threshold

        lastMobileTransmitted = TrafficStats.getMobileTxBytes()
        lastMobileReceived = TrafficStats.getMobileRxBytes()

        timer.schedule(timerTask, 0, timerPeriod)
    }
}