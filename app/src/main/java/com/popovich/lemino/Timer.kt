package com.popovich.lemino

import android.app.NotificationManager
import android.content.Context
import android.net.TrafficStats
import java.util.*

const val megabytesToBytesConversion = 1000000

const val defaultThresholdInBytes: Double = 0 * megabytesToBytesConversion.toDouble() // this needs match the string in the view

class MainTimer constructor(private val context: Context) {
    private val notificationActiveTime = 1100L// how long a notification will be alive
    private val timerPeriod = 1000L // Time period in ms that we check data usage

    private var thresholdInBytes = defaultThresholdInBytes

    private var timeNotificationPopped = 0L

    private var lastMobileReceived = 0L
    private var lastMobileTransmitted = 0L // sent

    private val notifications = Notifications(context)

    private val timer: Timer = Timer()
    private val timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            val bytesTransmitted = TrafficStats.getMobileTxBytes() - lastMobileTransmitted
            val bytesReceived = TrafficStats.getMobileRxBytes() - lastMobileReceived

            if (bytesTransmitted > thresholdInBytes || bytesReceived > thresholdInBytes) {
                val totalBytesUsed = (bytesReceived + bytesTransmitted).toDouble()
                val totalMegabytesUsed = totalBytesUsed / megabytesToBytesConversion

                notifications.showMainNotification(totalMegabytesUsed)

                timeNotificationPopped = System.currentTimeMillis()
            }

            if (System.currentTimeMillis() - timeNotificationPopped > notificationActiveTime) {
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.cancel(mainNotificationId)
            }

            lastMobileTransmitted = TrafficStats.getMobileTxBytes()
            lastMobileReceived = TrafficStats.getMobileRxBytes()
        }
    }

    fun stopTimerTask() {
        timer.cancel()
    }

    fun startTimerTask(thresholdInMB: Double) {
        this.thresholdInBytes = thresholdInMB * megabytesToBytesConversion

        lastMobileTransmitted = TrafficStats.getMobileTxBytes()
        lastMobileReceived = TrafficStats.getMobileRxBytes()

        timer.schedule(timerTask, 0, timerPeriod)
    }
}