package com.example.logbook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.logbook.R
import com.example.logbook.model.TaskItem

class TaskAdapter(context: Context, private val tasks: List<TaskItem>) :
    ArrayAdapter<TaskItem>(context, 0, tasks) {

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
        }

        return itemView!!
    }

    private class ViewHolder(view: View) {
        val taskNumber: TextView = view.findViewById(R.id.taskNumber)
        val taskDescription: TextView = view.findViewById(R.id.taskDescription)
        val taskDate: TextView = view.findViewById(R.id.taskDate)
    }

    override fun getItemId(position: Int): Long {
        return tasks[position].id ?: position.toLong()
    }
}