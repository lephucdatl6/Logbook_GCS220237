package com.example.logbook.data

import android.provider.BaseColumns

object TaskContract {
    object TaskEntry : BaseColumns {
        const val TABLE_NAME = "tasks"
        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_DATE_ADDED = "date_added"
    }
}