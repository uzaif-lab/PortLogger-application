package com.yourname.portlogger.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yourname.portlogger.utils.PowerStateManager
import java.util.Date

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device booted at ${Date()}")
            PowerStateManager(context).logPowerOn()
        }
    }
} 