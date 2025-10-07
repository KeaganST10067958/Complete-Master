package com.keagan.complete.data.notes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val text: String,
    val color: NoteColor = NoteColor.PEACH,
    val pinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class NoteColor { PEACH, MINT, BLUE, LAVENDER, LEMON }
