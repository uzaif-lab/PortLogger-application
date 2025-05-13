package com.yourname.portlogger.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LogDbHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private companion object {
        const val DATABASE_NAME = "DeviceLogs.db"
        const val DATABASE_VERSION = 3
        
        // Following Kotlin naming conventions for constants
        const val CREATE_TIMESTAMP_INDEX = "CREATE INDEX idx_timestamp ON ${LogContract.LogEntry.TABLE_NAME}" +
                " (${LogContract.LogEntry.COLUMN_TIMESTAMP})"
        const val CREATE_EVENT_TYPE_INDEX = "CREATE INDEX idx_event_type ON ${LogContract.LogEntry.TABLE_NAME}" +
                " (${LogContract.LogEntry.COLUMN_EVENT_TYPE})"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(LogContract.LogEntry.CREATE_TABLE)
        db.execSQL(CREATE_TIMESTAMP_INDEX)
        db.execSQL(CREATE_EVENT_TYPE_INDEX)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(LogContract.LogEntry.DROP_TABLE)
        onCreate(db)
    }

    fun insertLog(message: String, type: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(LogContract.LogEntry.COLUMN_EVENT, message)
            put(LogContract.LogEntry.COLUMN_EVENT_TYPE, type)
            put(LogContract.LogEntry.COLUMN_TIMESTAMP, System.currentTimeMillis())
        }
        db.insert(LogContract.LogEntry.TABLE_NAME, null, values)
    }

    fun insertFileLog(
        event: String,
        filePath: String,
        operationType: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(LogContract.LogEntry.COLUMN_EVENT, event)
            put(LogContract.LogEntry.COLUMN_EVENT_TYPE, LogContract.EventTypes.USB)
            put(LogContract.LogEntry.COLUMN_TIMESTAMP, System.currentTimeMillis())
            put(LogContract.LogEntry.COLUMN_FILE_PATH, filePath)
            put(LogContract.LogEntry.COLUMN_OPERATION_TYPE, operationType)
        }
        db.insert(LogContract.LogEntry.TABLE_NAME, null, values)
    }

    fun getAllLogs(): List<LogEntry> {
        val logs = mutableListOf<LogEntry>()
        val db = readableDatabase

        val cursor = db.query(
            LogContract.LogEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${LogContract.LogEntry.COLUMN_TIMESTAMP} DESC"
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val event = c.getString(c.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_EVENT))
                val type = c.getString(c.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_EVENT_TYPE))
                val timestamp = c.getLong(c.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_TIMESTAMP))
                logs.add(LogEntry(event, type, timestamp))
            }
        }

        return logs
    }

    fun getLogsByType(eventType: String): List<LogEntry> {
        val logs = mutableListOf<LogEntry>()
        val db = readableDatabase

        val selection = "${LogContract.LogEntry.COLUMN_EVENT_TYPE} = ?"
        val selectionArgs = arrayOf(eventType)

        val cursor = db.query(
            LogContract.LogEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "${LogContract.LogEntry.COLUMN_TIMESTAMP} DESC"
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val event = c.getString(c.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_EVENT))
                val type = c.getString(c.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_EVENT_TYPE))
                val timestamp = c.getLong(c.getColumnIndexOrThrow(LogContract.LogEntry.COLUMN_TIMESTAMP))
                logs.add(LogEntry(event, type, timestamp))
            }
        }

        return logs
    }

    fun deleteLogs(logs: List<LogEntry>) {
        val db = writableDatabase
        
        logs.forEach { log ->
            val selection = "${LogContract.LogEntry.COLUMN_EVENT} = ? AND " +
                    "${LogContract.LogEntry.COLUMN_EVENT_TYPE} = ? AND " +
                    "${LogContract.LogEntry.COLUMN_TIMESTAMP} = ?"
            val selectionArgs = arrayOf(
                log.event,
                log.type,
                log.timestamp.toString() // Convert Long to String for the query
            )
            
            db.delete(
                LogContract.LogEntry.TABLE_NAME,
                selection,
                selectionArgs
            )
        }
    }
} 