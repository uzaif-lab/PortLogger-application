package com.yourname.portlogger.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.BatteryManager
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper

class UsbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = LogDbHelper(context)
        
        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                dbHelper.insertLog("USB device connected", LogContract.EventTypes.USB)
            }
            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                dbHelper.insertLog("USB device disconnected", LogContract.EventTypes.USB)
            }
            Intent.ACTION_POWER_CONNECTED -> {
                // This could be either USB charging or wall charging
                if (isUsbCharging(context)) {
                    dbHelper.insertLog("USB charging connected", LogContract.EventTypes.USB)
                }
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                dbHelper.insertLog("USB/Power disconnected", LogContract.EventTypes.USB)
            }
        }
    }

    private fun isUsbCharging(context: Context): Boolean {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val plugged = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        return plugged == BatteryManager.BATTERY_PLUGGED_USB
    }
} 