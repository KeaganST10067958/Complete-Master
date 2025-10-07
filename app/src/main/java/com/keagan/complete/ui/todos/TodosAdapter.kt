package com.keagan.complete.ui.todos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keagan.complete.R
import com.keagan.complete.data.notes.NoteColor
import com.keagan.complete.data.todos.Todo
import com.keagan.complete.databinding.ItemTodoBinding

class TodosAdapter(
    private val onToggle: (Todo, Boolean) -> Unit,
    private val onDelete: (Todo) -> Unit
) : ListAdapter<Todo, TodosAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b, onToggle, onDelete)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(
        private val b: ItemTodoBinding,
        private val onToggle: (Todo, Boolean) -> Unit,
        private val onDelete: (Todo) -> Unit
    ) : RecyclerView.ViewHolder(b.root) {

        fun bind(item: Todo) {
            // card tint using NoteColor mapping
            b.root.setCardBackgroundColor(b.root.context.getColor(item.color.toColorRes()))
            b.tvTitle.text = item.title
            b.tvCategory.text = item.category.name.lowercase().replaceFirstChar { it.titlecase() }
            b.cbDone.setOnCheckedChangeListener(null)
            b.cbDone.isChecked = item.done
            b.cbDone.setOnCheckedChangeListener { _, checked -> onToggle(item, checked) }
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(o: Todo, n: Todo) = o.id == n.id
            override fun areContentsTheSame(o: Todo, n: Todo) = o == n
        }
    }
}

private fun NoteColor.toColorRes(): Int = when (this) {
    NoteColor.PEACH -> R.color.peach_200
    NoteColor.MINT -> R.color.mint_200
    NoteColor.BLUE -> R.color.blue_200
    NoteColor.LAVENDER -> R.color.lav_200
    NoteColor.LEMON -> R.color.lemon_200
}
