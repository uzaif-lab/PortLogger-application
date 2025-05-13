package com.yourname.portlogger.observers

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.Locale

class FileTransferObserver(private val context: Context) : ContentObserver(Handler(Looper.getMainLooper())) {
    private val dbHelper = LogDbHelper(context)
    private val fileStates = ConcurrentHashMap<String, Long>()

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        
        uri?.let {
            val path = getPathFromUri(it)
            if (path != null && isExternalStorageFile(path)) {
                val file = File(path)
                val operation = determineOperation(file)
                
                dbHelper.insertFileLog(
                    event = "File ${operation.lowercase(Locale.ROOT)}: ${file.name}",
                    filePath = file.absolutePath,
                    operationType = operation
                )
                
                // Update file state
                if (file.exists()) {
                    fileStates[path] = file.lastModified()
                } else {
                    fileStates.remove(path)
                }
            }
        }
    }

    private fun determineOperation(file: File): String {
        val path = file.absolutePath
        return when {
            !file.exists() -> LogContract.FileOperations.DELETED
            !fileStates.containsKey(path) -> LogContract.FileOperations.CREATED
            fileStates[path] != file.lastModified() -> LogContract.FileOperations.MODIFIED
            else -> LogContract.FileOperations.MODIFIED
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns.DATA),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun isExternalStorageFile(path: String): Boolean {
        return path.startsWith(context.getExternalFilesDir(null)?.parent ?: return false)
    }
} 