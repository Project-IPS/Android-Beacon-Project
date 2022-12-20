package com.example.ips1

import android.app.Application
import android.content.Context
import android.util.Log
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import org.altbeacon.beacon.startup.RegionBootstrap


/**
 * Created by Jaison on 02/03/17.
 */
class MyApplication : Application() {
    lateinit var regionBootstrap: RegionBootstrap
    lateinit var backgroundPowerSaver: BackgroundPowerSaver
    lateinit var beaconManager: BeaconManager
    lateinit var region: Region

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
//        appContext = applicationContext
        beaconManager = BeaconManager.getInstanceForApplication(this)

        // enables auto battery saving of about 60%
        backgroundPowerSaver = BackgroundPowerSaver(this)
    }

    companion object {
        private const val TAG = ".MyApplication"
        var appContext: Context? = null
            private set
        var isActive = false
    }
}