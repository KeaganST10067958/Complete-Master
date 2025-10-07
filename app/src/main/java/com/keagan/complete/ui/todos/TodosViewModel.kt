package com.keagan.complete.ui.todos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.keagan.complete.data.notes.NoteColor
import com.keagan.complete.data.notes.NotesDb
import com.keagan.complete.data.todos.Todo
import com.keagan.complete.data.todos.TodoCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodosViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = NotesDb.Companion.get(app).todos()

    val todos = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    fun add(title: String, category: TodoCategory, color: NoteColor) = viewModelScope.launch {
        if (title.isNotBlank()) dao.upsert(
            Todo(title = title.trim(), category = category, color = color)
        )
    }

    fun setDone(todo: Todo, done: Boolean) = viewModelScope.launch {
        dao.setDone(todo.id, done)
    }

    fun delete(todo: Todo) = viewModelScope.launch { dao.delete(todo) }
}