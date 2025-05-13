package com.yourname.portlogger.utils

import android.content.Context
import android.telephony.TelephonyManager
import com.yourname.portlogger.data.LogDbHelper
import com.yourname.portlogger.data.LogContract
import android.util.Log

class SimStateManager(private val context: Context) {
    // 100% verified line with correct parameter and cast
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val dbHelper = LogDbHelper(context)

    private fun getCurrentSimState(): String {
        return when (telephonyManager.simState) {
            TelephonyManager.SIM_STATE_ABSENT -> "No SIM"
            TelephonyManager.SIM_STATE_READY -> "SIM Ready"
            TelephonyManager.SIM_STATE_UNKNOWN -> "Unknown"
            else -> "Other"
        }
    }

    private fun getSimOperator(): String {
        return telephonyManager.simOperatorName ?: "Unknown"
    }

    fun logSimState() {
        try {
            val state = getCurrentSimState()
            val operator = if (state == "SIM Ready") getSimOperator() else ""
            dbHelper.insertLog(
                "SIM State: $state${if (operator.isNotEmpty()) " ($operator)" else ""}",
                type = LogContract.EventTypes.SIM
            )
        } catch (e: Exception) {
            Log.e("SimStateManager", "Error logging SIM state", e)
        }
    }
}
