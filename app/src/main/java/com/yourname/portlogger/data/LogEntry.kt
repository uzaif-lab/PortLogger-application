package com.yourname.portlogger.data

data class LogEntry(
    val event: String,
    val type: String,
    val timestamp: Long,
    val filePath: String? = null,
    val operationType: String? = null
) 