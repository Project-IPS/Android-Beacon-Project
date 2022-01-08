package com.example.ips1


interface BluetoothListener {
    fun initializeBluetooth(listener: OnBluetoothSupportedCheckListener?)
    fun enableBluetooth(listener: OnBluetoothEnabledCheckListener?)
    interface OnBluetoothSupportedCheckListener {
        fun onBLENotSupported()
        fun onBluetoothNotSupported()
    }

    interface OnBluetoothEnabledCheckListener {
        fun onBluetoothEnabled(enable: Boolean)
    }

    interface BluetoothTrigger {
        fun initBluetooth()
        fun enableBluetooth()
    }
}