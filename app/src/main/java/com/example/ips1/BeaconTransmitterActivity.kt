package com.example.ips1

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.example.ips1.BluetoothListener.*
import org.altbeacon.beacon.*
import java.util.*


class BeaconTransmitterActivity : AppCompatActivity(),
    OnBluetoothSupportedCheckListener, OnBluetoothEnabledCheckListener, BluetoothTrigger,
    BeaconConsumer {
    var btn_transmit: Button? = null
    var btn_apply: Button? = null
    var bluetoothHelper: BluetoothHelper? = null
    var etUUID: EditText? = null
    var etMajorValue: EditText? = null
    var etMinorValue: EditText? = null
    var isBluetoothEnabled = false
    var beacon: Beacon? = null
    var beaconParser: BeaconParser? = null
    var beaconTransmitter: BeaconTransmitter? = null
    private var beaconManager: BeaconManager? = null
    var beaconLayout = 0
    var beaconFormat = arrayOf("AltBeacon", "iBeacon")
    var parserLayout = arrayOf(
        "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25",
        "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    )
    var spin_beaconFormat: Spinner? = null
    var currentuuid: String? = null
    var currentmajorValue: String? = null
    var currentminorValue: String? = null
    var currentType = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_transmitter)
        bluetoothHelper = BluetoothHelper()
        bluetoothHelper!!.initializeBluetooth(this)
        val app = this.application as MyApplication
        beaconManager = app.beaconManager
        beaconManager!!.backgroundBetweenScanPeriod = 5000L
        beaconManager!!.foregroundBetweenScanPeriod = 5000L
        beaconManager!!.bind(this)
        btn_transmit = findViewById<View>(R.id.btn_transmit) as Button
        btn_apply = findViewById<View>(R.id.btn_apply) as Button
        etUUID = findViewById<View>(R.id.et_uuid) as EditText
        etMajorValue = findViewById<View>(R.id.et_major) as EditText
        etMinorValue = findViewById<View>(R.id.et_minor) as EditText
        etUUID!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                if (beaconTransmitter != null && s != currentuuid) btn_apply!!.isEnabled = true
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etMajorValue!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                if (beaconTransmitter != null && s != currentmajorValue) btn_apply!!.isEnabled =
                    true
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        etMinorValue!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                if (beaconTransmitter != null && s != currentminorValue) btn_apply!!.isEnabled =
                    true
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        spin_beaconFormat = findViewById<View>(R.id.spinner_lang) as Spinner
        val adapter1 = ArrayAdapter(
            this,
            R.layout.spinner_header_item, beaconFormat
        )
        adapter1.setDropDownViewResource(R.layout.spinner_item)
        spin_beaconFormat!!.adapter = adapter1
        spin_beaconFormat!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                beaconLayout = position
                if (beaconLayout != currentType) btn_apply!!.isEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        btn_transmit!!.setOnClickListener {
            if (isBluetoothEnabled) {
                try {
                    trasmitClick()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@BeaconTransmitterActivity,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else Toast.makeText(
                this@BeaconTransmitterActivity,
                "Check your bluetooth connection",
                Toast.LENGTH_LONG
            ).show()
        }
        btn_apply!!.setOnClickListener {
            btn_transmit!!.performClick()
            btn_transmit!!.performClick()
        }
    }

    fun trasmitClick() {
        if (beaconTransmitter == null) {
            var major: String
            var minor: String
            var uuid: String
            uuid = etUUID!!.text.toString().trim { it <= ' ' }
            major = etMajorValue!!.text.toString().trim { it <= ' ' }
            minor = etMinorValue!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(uuid)) uuid = "94339309-bfe2-4807-b747-9aee23508620"
            if (TextUtils.isEmpty(major)) major = "8"
            if (TextUtils.isEmpty(minor)) minor = "2"
            currentType = beaconLayout
            currentuuid = uuid
            currentmajorValue = major
            currentminorValue = minor
            beacon = Beacon.Builder()
                .setId1(uuid)
                .setId2(major)
                .setId3(minor)
                .setManufacturer(0x0118) // It is for AltBeacon.  Change this for other beacon layouts
                .setTxPower(-59)
                .setDataFields(
                    Arrays.asList(
                        *arrayOf(
                            6L,
                            7L
                        )
                    )
                ) // Remove this for beacon layouts without d: fields
                .build()

            // Change the layout below for other beacon types
            beaconParser = BeaconParser()
                .setBeaconLayout(parserLayout[beaconLayout])
            beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
            beaconTransmitter!!.startAdvertising(beacon, object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    super.onStartSuccess(settingsInEffect)
                }

                override fun onStartFailure(errorCode: Int) {
                    super.onStartFailure(errorCode)
                }
            })
            btn_transmit!!.text = "Stop Advertising"
            btn_apply!!.isEnabled = false
        } else {
            beaconTransmitter!!.startAdvertising()
            beaconTransmitter = null
            btn_transmit!!.text = "Start Advertising"
            btn_apply!!.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBLENotSupported() {
        Toast.makeText(this@BeaconTransmitterActivity, "BLE not supported", Toast.LENGTH_LONG)
            .show()
    }

    override fun onBluetoothNotSupported() {
        Toast.makeText(this@BeaconTransmitterActivity, "Blutooth not supported", Toast.LENGTH_LONG)
            .show()
    }

    override fun onBluetoothEnabled(enable: Boolean) {
        if (enable) {
            isBluetoothEnabled = true
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }
    }

    override fun initBluetooth() {
        if (bluetoothHelper != null) bluetoothHelper!!.initializeBluetooth(this)
    }

    override fun enableBluetooth() {
        if (bluetoothHelper != null) bluetoothHelper!!.enableBluetooth(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_CANCELED) {
            Toast.makeText(
                this@BeaconTransmitterActivity,
                "Bluetooth permission denied",
                Toast.LENGTH_LONG
            ).show()
            bluetoothHelper = null
            return
        } else {
            isBluetoothEnabled = true
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        if (bluetoothHelper != null) bluetoothHelper!!.enableBluetooth(this)
        MyApplication.isActive = true
    }

    override fun onPause() {
        super.onPause()
        MyApplication.isActive = false
    }

    override fun onBeaconServiceConnect() {}

    companion object {
        protected const val TAG = "Beacon Transmitter"
        private const val REQUEST_ENABLE_BLUETOOTH = 1
    }
}