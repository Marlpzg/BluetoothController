package com.sultral.controller

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.app.ProgressDialog
import android.os.AsyncTask
import android.content.Intent
import android.widget.Toast
//import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_control.*
import java.io.IOException
import java.util.*
import androidx.core.os.HandlerCompat.postDelayed




@Suppress("DEPRECATION")
class Control : AppCompatActivity() {

    var address: String? = null
    private var progress: ProgressDialog? = null
    var myBluetooth: BluetoothAdapter? = null
    var btSocket: BluetoothSocket? = null
    private var isBtConnected = false
    //SPP UUID. Look for it
    val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newint = intent
        address = newint.getStringExtra("device_address") //receive the address of the bluetooth device

        //view of the ledControl
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setContentView(R.layout.activity_control)


        ConnectBT().execute() //Call the class to connect

        //commands to be sent to bluetooth
        btn_frwd.setOnTouchListener(RepeatListener(100, 100, View.OnClickListener {
            sendAction("1")
        }))

    }

    override fun onDestroy() {
        Disconnect()
        super.onDestroy()
    }


    private fun sendAction(action: String) {
        if (btSocket != null) {
            try {
                btSocket!!.outputStream.write(action.toByteArray())
            } catch (e: IOException) {
                msg("Error")
            }

        }
    }

    private fun Disconnect() {
        if (btSocket != null)
        //If the btSocket is busy
        {
            try {
                btSocket?.close() //close connection
            } catch (e: IOException) {
                msg("Error")
            }

        }
        finish() //return to the first layout

    }

    // fast way to call Toast
    private fun msg(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_LONG).show()
    }



    private inner class ConnectBT : AsyncTask<Void, Void, Void>  // UI thread
        () {
        private var ConnectSuccess = true //if it's here, it's almost connected

        override fun onPreExecute() {
            progress = ProgressDialog.show(this@Control, "Connecting...", "Please wait!!!")  //show a progress dialog
        }

        override fun doInBackground(vararg devices: Void) //while the progress dialog is shown, the connection is done in background
                : Void? {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter()//get the mobile bluetooth device
                    val dispositivo = myBluetooth!!.getRemoteDevice(address)//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID)//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    btSocket!!.connect()//start connection
                }
            } catch (e: IOException) {
                println("ERROR!")
                ConnectSuccess = false//if the try failed, you can check the exception here
            }

            return null
        }

        override fun onPostExecute(result: Void?) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result)

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.")
                finish()
            } else {
                msg("Connected.")
                isBtConnected = true
            }
            progress!!.dismiss()
        }
    }

}
