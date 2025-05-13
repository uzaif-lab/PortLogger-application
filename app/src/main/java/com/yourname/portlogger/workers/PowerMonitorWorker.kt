package com.yourname.portlogger.workers

import android.content.Context
import android.os.PowerManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper

class PowerMonitorWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val dbHelper = LogDbHelper(applicationContext)
        
        // Check power state and log only if changed
        // This reduces unnecessary database writes
        val isScreenOn = powerManager.isInteractive
        val lastState = applicationContext.getSharedPreferences(
            "power_prefs", Context.MODE_PRIVATE
        ).getBoolean("last_power_state", true)

        if (isScreenOn != lastState) {
            dbHelper.insertLog(
                if (isScreenOn) "Device powered on" else "Device powered off",
                LogContract.EventTypes.POWER
            )
            
            applicationContext.getSharedPreferences("power_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("last_power_state", isScreenOn)
                .apply()
        }

        return Result.success()
    }
} 