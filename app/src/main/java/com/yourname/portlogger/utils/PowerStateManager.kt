package com.yourname.portlogger.utils

import android.content.Context
import android.content.SharedPreferences
import com.yourname.portlogger.data.LogDbHelper
import com.yourname.portlogger.data.LogContract

class PowerStateManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        POWER_PREFS, Context.MODE_PRIVATE
    )
    private val dbHelper = LogDbHelper(context)

    fun recordActivityTimestamp() {
        prefs.edit().putLong(LAST_ACTIVITY_TIME, System.currentTimeMillis()).apply()
    }

    private fun getLastActivityTime(): Long {
        return prefs.getLong(LAST_ACTIVITY_TIME, 0)
    }

    fun checkPowerGap(): Boolean {
        val lastActivity = getLastActivityTime()
        val currentTime = System.currentTimeMillis()
        
        // If gap is more than 1 minute, assume device was powered off
        // You can adjust this threshold as needed
        return if (lastActivity > 0) {
            val gap = currentTime - lastActivity
            if (gap > POWER_OFF_THRESHOLD) {
                dbHelper.insertLog(
                    "Device potentially powered off for ${gap/1000} seconds",
                    LogContract.EventTypes.POWER
                )
                true
            } else false
        } else false
    }

    fun logPowerOn() {
        dbHelper.insertLog("Device powered on", LogContract.EventTypes.POWER)
    }

    companion object {
        private const val POWER_PREFS = "power_prefs"
        private const val LAST_ACTIVITY_TIME = "last_activity_time"
        private const val POWER_OFF_THRESHOLD = 60000L // 1 minute in milliseconds
    }
} 