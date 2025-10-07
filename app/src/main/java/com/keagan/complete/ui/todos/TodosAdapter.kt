package com.keagan.complete.ui.todos

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.keagan.complete.databinding.RowTodoBinding

data class Todo(
    val id: String,
    val title: String,
    val category: String? = null,
    var done: Boolean = false
)

class TodosAdapter(
    private val onToggle: (Todo) -> Unit,
    private val onDelete: (Todo) -> Unit
) : ListAdapter<Todo, TodosAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(old: Todo, new: Todo) = old.id == new.id
            override fun areContentsTheSame(old: Todo, new: Todo) = old == new
        }
    }

    inner class VH(val b: RowTodoBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Todo) {
            b.tvTitle.text = item.title
            b.tvCategory.text = item.category ?: ""

            b.cbDone.setOnCheckedChangeListener(null)
            b.cbDone.isChecked = item.done

            // strike through + subtle grey when done
            b.tvTitle.paintFlags =
                if (item.done) b.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else b.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            b.root.alpha = if (item.done) 0.55f else 1f

            b.cbDone.setOnCheckedChangeListener { _, _ -> onToggle(item) }
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        return VH(RowTodoBinding.inflate(inf, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
