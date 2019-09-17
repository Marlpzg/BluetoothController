package com.sultral.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timerThread = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(700)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    val intent = Intent(this@MainActivity, DeviceList::class.java)
                    startActivity(intent)
                }
            }
        }
        timerThread.start()
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
        finish()
    }
}
