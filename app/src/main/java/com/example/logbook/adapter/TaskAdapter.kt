package com.example.logbook.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.logbook.R
import com.example.logbook.data.TaskDbHelper
import com.example.logbook.model.TaskItem

class TaskAdapter(context: Context, private val tasks: List<TaskItem>) :
    ArrayAdapter<TaskItem>(context, 0, tasks) {

    private val dbHelper = TaskDbHelper(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val viewHolder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
        } else {
            viewHolder = itemView.tag as ViewHolder
        }

        val currentTask = getItem(position)

        if (currentTask != null) {
            viewHolder.taskNumber.text = "${position + 1}."
            viewHolder.taskDescription.text = currentTask.description
            viewHolder.taskDate.text = currentTask.dateAdded

            // Avoid triggering listener when setting checked state
            viewHolder.taskCheckBox.setOnCheckedChangeListener(null)
            viewHolder.taskCheckBox.isChecked = currentTask.isDone

            // Strike-through text if task is marked done
            viewHolder.taskDescription.paintFlags = if (currentTask.isDone) {
                viewHolder.taskDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                viewHolder.taskDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Update DB and UI when checkbox is toggled
            viewHolder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                currentTask.id?.let { id ->
                    dbHelper.updateTaskStatus(id, isChecked)
                    currentTask.isDone = isChecked

                    // Update strike-through
                    viewHolder.taskDescription.paintFlags = if (isChecked) {
                        viewHolder.taskDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        viewHolder.taskDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }
            }
        }

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val taskNumber: TextView = view.findViewById(R.id.taskNumber)
        val taskDescription: TextView = view.findViewById(R.id.taskDescription)
        val taskDate: TextView = view.findViewById(R.id.taskDate)
        val taskCheckBox: CheckBox = view.findViewById(R.id.taskCheckBox)
    }

    override fun getItemId(position: Int): Long {
        return tasks[position].id ?: position.toLong()
    }
}
