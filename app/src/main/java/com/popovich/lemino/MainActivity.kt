package com.popovich.lemino

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val business = Business()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.start_button).setOnClickListener {
            business.stopMainServiceAndCancelNotification(applicationContext)
            business.startMainService(applicationContext, this)
        }

        findViewById<Button>(R.id.stop_button).setOnClickListener {
            business.stopMainServiceAndCancelNotification(applicationContext)
        }
    }
}
