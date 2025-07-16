package com.example.logbook.activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.logbook.R
import com.example.logbook.adapter.TaskAdapter
import com.example.logbook.data.TaskDbHelper
import com.example.logbook.model.TaskItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodoList : AppCompatActivity() {
    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var listViewTasks: ListView

    // Use ArrayList to store TaskItem
    private val tasksList = ArrayList<TaskItem>()
    private lateinit var adapter: TaskAdapter

    // Database Helper
    private lateinit var dbHelper: TaskDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        listViewTasks = findViewById(R.id.listViewTasks)
        val textViewClearAll = findViewById<TextView>(R.id.textViewClearAll)


        dbHelper = TaskDbHelper(this)

        adapter = TaskAdapter(this, tasksList)
        listViewTasks.adapter = adapter

        loadTasksFromDb()

        buttonAdd.setOnClickListener {
            val taskDescription = editTextTask.text.toString().trim()
            if (taskDescription.isNotEmpty()) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                    Date()
                )
                val newTask = TaskItem(description = taskDescription, dateAdded = currentDate)

                val newRowId = dbHelper.insertTask(newTask)
                if (newRowId > -1) {
                    loadTasksFromDb()
                    editTextTask.text.clear()
                    Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }

        textViewClearAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear All Tasks")
                .setMessage("Are you sure you want to delete all tasks?")
                .setPositiveButton("Yes") { _, _ ->
                    val deletedCount = dbHelper.deleteAllTasks()
                    if (deletedCount > 0) {
                        tasksList.clear()
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No tasks to delete", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }


        listViewTasks.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = tasksList[position]
            showEditDialog(selectedTask, position)
        }

        listViewTasks.setOnItemLongClickListener { _, _, position, _ ->
            val taskToDelete = tasksList[position]
            AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task: [${taskToDelete.description}]")
                .setPositiveButton("Yes") { _, _ ->
                    taskToDelete.id?.let { id ->
                        val deletedRows = dbHelper.deleteTask(id)
                        if (deletedRows > 0) {
                            loadTasksFromDb()
                            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(this, "Task ID not found, cannot delete", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }


    private fun loadTasksFromDb() {
        val tasksFromDb = dbHelper.getAllTasks()
        tasksList.clear()
        tasksList.addAll(tasksFromDb)
        adapter.notifyDataSetChanged()
    }

    private fun showEditDialog(taskItem: TaskItem, position: Int) {
        val editText = EditText(this)
        editText.setText(taskItem.description)

        Log.d("TodoList", "showEditDialog for Task: ID=${taskItem.id}, Desc=${taskItem.description}")

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newTaskDescription = editText.text.toString().trim()
                if (newTaskDescription.isNotEmpty()) {
                    if (taskItem.id == null) {
                        Toast.makeText(this, "Cannot update task: Original ID is missing.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val updatedTask = TaskItem(
                        id = taskItem.id,
                        description = newTaskDescription,
                        dateAdded = taskItem.dateAdded
                    )

                    val rowsAffected = dbHelper.updateTask(updatedTask)
                    if (rowsAffected > 0) {
                        loadTasksFromDb()
                        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Task not updated (no changes or error)", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}