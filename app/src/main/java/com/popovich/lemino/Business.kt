package com.popovich.lemino

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.widget.EditText
import androidx.core.app.NotificationCompat
import java.util.*

private const val timerPeriod = 1000L //
private const val notificationActiveTime = timerPeriod + 2000L // how long a notification will be alive, needs to be greater than timer period

class Business {
    private val notifications = Notifications()

    fun startMainServiceBusinessLogic(context: Context, intent: Intent) {
        startTimerTask(context, intent.getDoubleExtra(context.getString(R.string.threshold_in_mb_key), 0.0))
    }

    fun startMainService(context: Context, mainActivity: MainActivity) {
        val threshold: EditText = mainActivity.findViewById(R.id.threshold)

        if (threshold.text != null && threshold.text.toString().toDoubleOrNull() != null) {
            val serviceIntent = Intent(context, MainService::class.java)
            serviceIntent.putExtra(context.getString(R.string.threshold_in_mb_key), threshold.text.toString().toDouble())

            startServiceAppropriately(context, serviceIntent)
        }
    }

    fun stopMainServiceAndCancelNotification(context: Context) {
        context.stopService(Intent(context, MainService::class.java))

        notifications.cancelNotification(context, context.resources.getInteger(R.integer.mainNotificationId))
    }

    fun buildForegroundServiceNotificationAndChannel(context: Context): Notification? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            notifications.createNotificationChannel(context, R.string.service_channel_id, R.string.service_channel_description, NotificationManager.IMPORTANCE_DEFAULT)
        else
            notifications.createNotificationChannel(context, R.string.service_channel_id, R.string.service_channel_description, 0)


        return NotificationCompat.Builder(context, context.getString(R.string.service_channel_id))
            .setContentTitle("")
            .setContentText("").build()
    }

    fun getKillServicePendingIntent(context: Context): PendingIntent {
        val broadcastIntent = Intent()
        broadcastIntent.action = context.getString(R.string.kill_service_action)
        broadcastIntent.setClass(context, ServiceStopper::class.java)

        return PendingIntent.getBroadcast(context, 0, broadcastIntent, 0)
    }

    private fun startServiceAppropriately(context: Context, serviceIntent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private val timer: Timer = Timer()
    private lateinit var timerTask: MainContextTimerTask

    private fun startTimerTask(context: Context, thresholdInMB: Double) {
        timerTask = MainContextTimerTask(context, thresholdInMB * context.resources.getInteger(R.integer.megabytesToBytesConversion))

        timer.schedule(timerTask, 0, timerPeriod)
    }

    fun killMainTimerTask() {
        timer.cancel()
    }
}

class ServiceStopper: BroadcastReceiver() {
    private val business = Business()

    override fun onReceive(context: Context, intent: Intent) {
        business.stopMainServiceAndCancelNotification(context)
    }
}

class MainContextTimerTask(private val context: Context, private val thresholdInBytes: Double) : TimerTask() {
    private val notifications = Notifications()

    private val business = Business()

    private var timeNotificationPopped = 0L

    private var lastMobileReceived = TrafficStats.getMobileRxBytes()
    private var lastMobileTransmitted = TrafficStats.getMobileTxBytes() // sent

    private val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private val mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0)

    override fun run() {
        val bytesTransmitted = TrafficStats.getMobileTxBytes() - lastMobileTransmitted
        val bytesReceived = TrafficStats.getMobileRxBytes() - lastMobileReceived

        if (bytesTransmitted > thresholdInBytes || bytesReceived > thresholdInBytes) {
            val totalBytesUsed = (bytesReceived + bytesTransmitted).toDouble()
            val totalMegabytesUsed = totalBytesUsed / context.resources.getInteger(R.integer.megabytesToBytesConversion)

            var importance = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                importance = NotificationManager.IMPORTANCE_LOW
            }

            notifications.showNotification(
                context,
                R.string.channel_id,
                R.string.channel_name,
                importance,
                context.resources.getInteger(R.integer.mainNotificationId),
                R.drawable.ic_stat_onesignal_default,
                context.getString(R.string.main_notification_title),
                context.getString(R.string.main_notification_content, String.format("%.2f", totalMegabytesUsed)),
                NotificationCompat.Action(R.drawable.ic_stat_onesignal_default,
                    context.getString(R.string.STOP_LISTENING),
                    business.getKillServicePendingIntent(context)),
                mainActivityPendingIntent,
                NotificationCompat.PRIORITY_MIN,
                NotificationCompat.VISIBILITY_PUBLIC,
                true
            )

            timeNotificationPopped = System.currentTimeMillis()
        }

        if (System.currentTimeMillis() - timeNotificationPopped > notificationActiveTime) {
            notifications.cancelNotification(context, context.resources.getInteger(R.integer.mainNotificationId))
        }

        lastMobileTransmitted = TrafficStats.getMobileTxBytes()
        lastMobileReceived = TrafficStats.getMobileRxBytes()
    }
}