package com.example.ips1

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
//import droidmentor.beacontransmitter.MyApplication
import com.example.ips1.MyApplication


/**
 * Created by Jaison on 05/04/17.
 */
class BluetoothHelper : BluetoothListener {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    @JvmName("initializeBluetooth1")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun initializeBluetooth(listener: BluetoothListener.OnBluetoothSupportedCheckListener) {

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
//        if (!MyApplication.getAppContext().getPackageManager()
//                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
//        ) {
//            listener.onBLENotSupported()
//            return
//        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
//        val bluetoothManager = MyApplication.getAppContext()
//            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        mBluetoothAdapter = bluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            listener.onBluetoothNotSupported()
        }
    }

    override fun initializeBluetooth(listener: BluetoothListener.OnBluetoothSupportedCheckListener?) {
        TODO("Not yet implemented")
    }

    override fun enableBluetooth(listener: BluetoothListener.OnBluetoothEnabledCheckListener?) {
        if (mBluetoothAdapter != null) {
            val enabled = mBluetoothAdapter!!.isEnabled
            if (listener != null) {
                listener.onBluetoothEnabled(enabled)
            }
        }
    }
}