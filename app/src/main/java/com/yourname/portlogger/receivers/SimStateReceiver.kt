package com.yourname.portlogger.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper

class SimStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.SIM_STATE_CHANGED") {
            val simState = intent.getIntExtra("sim_state", TelephonyManager.SIM_STATE_UNKNOWN)
            val dbHelper = LogDbHelper(context)
            
            val message = when (simState) {
                TelephonyManager.SIM_STATE_ABSENT -> "No SIM card present"
                TelephonyManager.SIM_STATE_READY -> {
                    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                    val simOperator = telephonyManager?.simOperatorName ?: "Unknown"
                    "SIM card ready: $simOperator"
                }
                TelephonyManager.SIM_STATE_PIN_REQUIRED -> "SIM PIN required"
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> "SIM PUK required"
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "SIM network locked"
                TelephonyManager.SIM_STATE_NOT_READY -> "SIM not ready"
                TelephonyManager.SIM_STATE_PERM_DISABLED -> "SIM permanently disabled"
                TelephonyManager.SIM_STATE_CARD_IO_ERROR -> "SIM card I/O error"
                TelephonyManager.SIM_STATE_CARD_RESTRICTED -> "SIM card restricted"
                TelephonyManager.SIM_STATE_UNKNOWN -> "SIM state unknown"
                else -> "Unknown SIM state: $simState"
            }
            
            Log.d("SimStateReceiver", "SIM state changed: $message")
            dbHelper.insertLog(message, LogContract.EventTypes.SIM)
        }
    }
} 