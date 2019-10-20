package com.popovich.lemino

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.start_button).setOnClickListener {
            if (!isServiceRunning(MainService::class.java))
                startMainService(applicationContext)
        }

        findViewById<Button>(R.id.stop_button).setOnClickListener {
            applicationContext.stopService(Intent(applicationContext, MainService::class.java))

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.cancel(mainNotificationId)
        }
    }

    private fun startMainService(context: Context) {
        // val threshold: EditText  = findViewById(R.id.threshold)

        val serviceIntent = Intent(context, MainService::class.java)
        // serviceIntent.putExtra(context.getString(R.string.threshold_key), threshold.text.toString().toInt())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    // https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
