package com.yourname.portlogger.data


object LogContract {
    object LogEntry {
        const val TABLE_NAME = "logs"
        const val COLUMN_EVENT = "event"
        const val COLUMN_EVENT_TYPE = "event_type"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_FILE_PATH = "file_path"
        const val COLUMN_OPERATION_TYPE = "operation_type"

        const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_EVENT TEXT NOT NULL,
                $COLUMN_EVENT_TYPE TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_FILE_PATH TEXT,
                $COLUMN_OPERATION_TYPE TEXT
            )
        """

        const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    object EventTypes {
        const val POWER = "power"
        const val USB = "usb"
        const val SIM = "sim"
    }

    object FileOperations {
        const val CREATED = "CREATED"
        const val MODIFIED = "MODIFIED"
        const val DELETED = "DELETED"
    }
} 