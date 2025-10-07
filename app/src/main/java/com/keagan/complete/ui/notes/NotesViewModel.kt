package com.keagan.complete.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.keagan.complete.data.notes.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NotesTab { ALL, PINNED }

@OptIn(FlowPreview::class)
class NotesViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = NotesDb.get(app).notes()

    private val query = MutableStateFlow("")
    private val tab = MutableStateFlow(NotesTab.ALL)

    val notes: StateFlow<List<Note>> =
        combine(query.debounce(200), tab) { q, t -> q to t }
            .flatMapLatest { (q, t) ->
                when {
                    q.isNotBlank() -> dao.searchAll("%${q.trim()}%")
                    t == NotesTab.PINNED -> dao.observePinned()
                    else -> dao.observeAll()
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setQuery(q: String) { query.value = q }
    fun setTab(t: NotesTab) { tab.value = t }

    fun add(title: String, text: String, color: NoteColor) = viewModelScope.launch {
        dao.upsert(Note(title = title.trim(), text = text.trim(), color = color))
    }

    fun delete(n: Note) = viewModelScope.launch { dao.delete(n) }

    /** used by swipe-undo to put the same note back (keeps the old id) */
    fun restore(n: Note) = viewModelScope.launch { dao.upsert(n) }

    fun togglePin(n: Note) = viewModelScope.launch { dao.setPinned(n.id, !n.pinned) }
}
