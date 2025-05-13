package com.yourname.portlogger.utils


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.provider.MediaStore
import com.yourname.portlogger.observers.FileTransferObserver
import com.yourname.portlogger.receivers.UsbReceiver
import android.util.Log

class UsbMonitor(private val context: Context) {
    private var contentObserver: FileTransferObserver? = null
    private var usbReceiver: UsbReceiver? = null

    fun startMonitoring() {
        usbReceiver = UsbReceiver().also { receiver ->
            val filter = IntentFilter().apply {
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            context.registerReceiver(receiver, filter)
        }

        // Start file transfer monitoring
        contentObserver = FileTransferObserver(context).also {
            context.contentResolver.registerContentObserver(
                MediaStore.Files.getContentUri("external"),
                true,
                it
            )
        }
    }

    fun stopMonitoring() {
        usbReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                Log.e("UsbMonitor", "Error unregistering receiver", e)
            }
        }
        usbReceiver = null
        
        contentObserver?.let {
            context.contentResolver.unregisterContentObserver(it)
        }
        contentObserver = null
    }
} 