package com.popovich.lemino

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.start_button).setOnClickListener {
            stopMainService()
            startMainService(applicationContext)
        }

        findViewById<Button>(R.id.stop_button).setOnClickListener {
            stopMainService()
        }
    }

    private fun startMainService(context: Context) {
        val threshold: EditText = findViewById(R.id.threshold)

        val serviceIntent = Intent(context, MainService::class.java)
        serviceIntent.putExtra(context.getString(R.string.threshold_key), threshold.text.toString().toDouble())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun stopMainService() {
        applicationContext.stopService(Intent(applicationContext, MainService::class.java))

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(mainNotificationId)
    }
}

