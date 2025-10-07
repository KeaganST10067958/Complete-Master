package com.keagan.complete.data.todos

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.keagan.complete.data.notes.NoteColor

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val category: TodoCategory = TodoCategory.WORK,
    val color: NoteColor = NoteColor.PEACH,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TodoCategory { STUDY, WORK, PERSONAL, MISC }
