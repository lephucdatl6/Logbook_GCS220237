package com.example.logbook.model

data class TaskItem(
    var description: String,
    var dateAdded: String,
    var id: Long? = null
) {
    override fun toString(): String {
        return "$description ($dateAdded)"
    }
}