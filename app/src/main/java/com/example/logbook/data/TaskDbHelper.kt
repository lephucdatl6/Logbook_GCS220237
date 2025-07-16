package com.example.logbook.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.logbook.data.TaskContract
import com.example.logbook.model.TaskItem

class TaskDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Tasks.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${TaskContract.TaskEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION} TEXT," +
                    "${TaskContract.TaskEntry.COLUMN_NAME_DATE_ADDED} TEXT," +
                    "isDone INTEGER DEFAULT 0)"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${TaskContract.TaskEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    // --- CRUD Operations ---
    fun insertTask(task: TaskItem): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION, task.description)
            put(TaskContract.TaskEntry.COLUMN_NAME_DATE_ADDED, task.dateAdded)
            put("isDone", if (task.isDone) 1 else 0)
        }
        val newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun getAllTasks(): ArrayList<TaskItem> {
        val taskList = ArrayList<TaskItem>()
        val db = readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
            TaskContract.TaskEntry.COLUMN_NAME_DATE_ADDED,
            "isDone"
        )

        val sortOrder = "${TaskContract.TaskEntry.COLUMN_NAME_DATE_ADDED} DESC"
        val cursor = db.query(
            TaskContract.TaskEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID)) // Get the ID
                val description = getString(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION))
                val dateAdded = getString(getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_DATE_ADDED))

                val isDone = getInt(getColumnIndexOrThrow("isDone")) == 1
                val task = TaskItem(description = description, dateAdded = dateAdded, id = itemId, isDone = isDone)
                taskList.add(task)
            }
        }
        cursor.close()
        return taskList
    }

    fun updateTask(task: TaskItem): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TaskContract.TaskEntry.COLUMN_NAME_DESCRIPTION, task.description)

        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(task.id.toString())

        if (task.id == null) {
            Log.e("TaskDbHelper", "Attempted to update a task with a null ID: ${task.description}")
            return 0 // Or throw an exception
        }

        val count = db.update(
            TaskContract.TaskEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
        return count
    }

    fun deleteTask(taskId: Long): Int {
        val db = writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(taskId.toString())
        val deletedRows = db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs)
        return deletedRows
    }

    fun deleteAllTasks(): Int {
        val db = writableDatabase
        return db.delete("tasks", null, null)
    }

    fun updateTaskStatus(taskId: Long, isDone: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("isDone", if (isDone) 1 else 0)
        }
        return db.update(
            TaskContract.TaskEntry.TABLE_NAME,
            values,
            "${BaseColumns._ID} = ?",
            arrayOf(taskId.toString())
        )
    }
}