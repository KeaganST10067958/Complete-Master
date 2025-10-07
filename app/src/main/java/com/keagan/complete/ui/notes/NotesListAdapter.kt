package com.keagan.complete.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keagan.complete.R
import com.keagan.complete.data.notes.Note
import com.keagan.complete.data.notes.NoteColor
import com.keagan.complete.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())

class NotesListAdapter(
    private val onDelete: (Note) -> Unit,
    private val onPinToggle: (Note) -> Unit
) : ListAdapter<Note, NotesVH>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesVH {
        val b = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesVH(b, onDelete, onPinToggle)
    }
    override fun onBindViewHolder(holder: NotesVH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
        }
    }
}

class NotesVH(
    private val b: ItemNoteBinding,
    private val onDelete: (Note) -> Unit,
    private val onPinToggle: (Note) -> Unit
) : RecyclerView.ViewHolder(b.root) {

    fun bind(n: Note) {
        b.card.setCardBackgroundColor(b.root.context.getColor(n.color.toColorRes()))
        b.txtTitle.text = n.title.ifBlank { b.root.context.getString(R.string.untitled) }
        b.txtSnippet.text = n.text
        b.txtDate.text = sdf.format(Date(n.createdAt))
        b.btnDelete.setOnClickListener { onDelete(n) }
        b.btnPin.setImageResource(if (n.pinned) R.drawable.ic_pin_filled else R.drawable.ic_pin)
        b.btnPin.setOnClickListener { onPinToggle(n) }
    }
}

private fun NoteColor.toColorRes(): Int = when (this) {
    NoteColor.PEACH -> R.color.peach_200
    NoteColor.MINT -> R.color.mint_200
    NoteColor.BLUE -> R.color.blue_200
    NoteColor.LAVENDER -> R.color.lav_200
    NoteColor.LEMON -> R.color.lemon_200
}
