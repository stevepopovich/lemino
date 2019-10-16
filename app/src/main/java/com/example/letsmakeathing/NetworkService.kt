package com.example.letsmakeathing

import android.app.NotificationChannel
import android.app.NotificationManager
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




class NetworkService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    var lastMobileReceived = 0L
    var lastMobileTransmitted = 0L // sent

    var timeNotificationPopped = 0L

    private val mainNotificationId = 0

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        lastMobileTransmitted = TrafficStats.getMobileTxBytes()
        lastMobileReceived = TrafficStats.getMobileRxBytes()

        Log.e("letsmakeathing", "created network service")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("letsmakeathing", "started network service")

        startTimer()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        stopTimerTask()
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private fun startTimer() {
        timer = Timer()

        initializeTimerTask()

        timer!!.schedule(timerTask, 0, 500) //
    }

    private fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
                if (TrafficStats.getMobileTxBytes() - lastMobileTransmitted > 0)
                    Log.e("a thing", "Timestamp" + Calendar.getInstance().time + " difference of transmitted is:" + (TrafficStats.getMobileTxBytes() - lastMobileTransmitted).toString())

                if (TrafficStats.getMobileRxBytes() - lastMobileReceived > 0)
                    Log.e("a thing", "Timestamp" + Calendar.getInstance().time + " difference of received is:" + (TrafficStats.getMobileRxBytes() - lastMobileReceived).toString())

                if (TrafficStats.getMobileTxBytes() - lastMobileTransmitted > 0 || TrafficStats.getMobileRxBytes() - lastMobileReceived > 0) {
                    buildNotification()

                    timeNotificationPopped = System.currentTimeMillis()
                }

                if (System.currentTimeMillis() - timeNotificationPopped > 1500) {
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    notificationManager.cancel(mainNotificationId)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(resources.getString(R.string.channel_name), name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification() {
        val builder = NotificationCompat.Builder(this, resources.getString(R.string.channel_name))
            .setSmallIcon(R.drawable.ic_stat_onesignal_default)
            .setContentTitle("You are using data!")
            .setContentText("MITIGATE")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(this)) {
            notify(mainNotificationId, builder.build())
        }
    }

    private fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }
}
