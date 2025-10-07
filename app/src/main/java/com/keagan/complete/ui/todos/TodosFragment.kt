package com.keagan.complete.ui.todos

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keagan.complete.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class TodosFragment : Fragment() {

    data class Todo(
        val id: String = UUID.randomUUID().toString(),
        var title: String,
        var category: String,
        var color: Int,
        var done: Boolean = false,
        val createdAt: Long = System.currentTimeMillis()
    )

    private val prefs by lazy { requireContext().getSharedPreferences("todos_store", 0) }
    private val keyTodos = "todos_json"

    private lateinit var rv: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnAdd: View
    private lateinit var btnActive: Button
    private lateinit var btnDone: Button

    private val all = mutableListOf<Todo>()
    private var showingDone = false
    private lateinit var adapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_todos, container, false)

        rv = v.findViewById(R.id.rvTodos)
        etSearch = v.findViewById(R.id.etSearch)
        btnAdd = v.findViewById(R.id.btnAdd)
        btnActive = v.findViewById(R.id.btnActive)
        btnDone = v.findViewById(R.id.btnDone)

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = TodoAdapter(
            onToggle = { t, checked -> t.done = checked; save(); refresh() },
            onDelete = { t -> all.removeAll { it.id == t.id }; save(); refresh() }
        )
        rv.adapter = adapter

        load()
        refresh()

        btnAdd.setOnClickListener { showAddTodoSheet() }
        btnActive.setOnClickListener { showingDone = false; refresh() }
        btnDone.setOnClickListener { showingDone = true; refresh() }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { refresh() }
            override fun afterTextChanged(s: Editable?) {}
        })

        return v
    }

    private fun showAddTodoSheet() {
        // 1) Make sure the file is at res/layout/dialog_add_todo.xml
        val dialog: Dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_add_todo, null, false)
        dialog.setContentView(view)

        val inputTitle = view.findViewById<EditText>(R.id.inputTitle)
        val chipsCategory = view.findViewById<ChipGroup>(R.id.chipsCategory)
        val chipsColor = view.findViewById<ChipGroup>(R.id.chipsColor)
        val previewCard = view.findViewById<MaterialCardView>(R.id.previewCard)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        var chosenCategory = getString(R.string.category_misc)
        var chosenColor = ContextCompat.getColor(requireContext(), R.color.peach_200)
        fun updatePreview() = previewCard.setCardBackgroundColor(chosenColor)
        updatePreview()

        // Material 1.10: OnCheckedStateChangeListener (list of ids)
        chipsCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            val id = checkedIds.firstOrNull() ?: View.NO_ID
            chosenCategory = when (id) {
                R.id.chipStudy -> getString(R.string.category_study)
                R.id.chipWork -> getString(R.string.category_work)
                R.id.chipPersonal -> getString(R.string.category_personal)
                R.id.chipMisc -> getString(R.string.category_misc)
                else -> getString(R.string.category_misc)
            }
        }

        chipsColor.setOnCheckedStateChangeListener { group, checkedIds ->
            val id = checkedIds.firstOrNull() ?: View.NO_ID
            chosenColor = when (id) {
                R.id.chipPeach -> ContextCompat.getColor(requireContext(), R.color.peach_200)
                R.id.chipMint  -> ContextCompat.getColor(requireContext(), R.color.mint_200)
                R.id.chipBlue  -> ContextCompat.getColor(requireContext(), R.color.blue_200)
                R.id.chipLav   -> ContextCompat.getColor(requireContext(), R.color.lav_200)
                R.id.chipLemon -> colorOrFallback(R.color.lemon_200, R.color.note_yellow_soft)
                else -> ContextCompat.getColor(requireContext(), R.color.peach_200)
            }
            updatePreview()
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener {
            val title = inputTitle.text?.toString()?.trim().orEmpty()
            if (title.isEmpty()) { inputTitle.error = getString(R.string.error_required); return@setOnClickListener }
            all.add(Todo(title = title, category = chosenCategory, color = chosenColor))
            save()
            refresh()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun colorOrFallback(primaryRes: Int, fallbackRes: Int): Int {
        // If a color resource doesn’t exist (e.g., lemon_200), use fallback.
        val id = try {
            resources.getColor(primaryRes, requireContext().theme)
            primaryRes
        } catch (_: Exception) {
            fallbackRes
        }
        return ContextCompat.getColor(requireContext(), id)
    }

    private fun refresh() {
        val q = etSearch.text?.toString()?.trim()?.lowercase().orEmpty()
        val filtered = all.asSequence()
            .filter { it.done == showingDone }
            .filter { q.isEmpty() || it.title.lowercase().contains(q) || it.category.lowercase().contains(q) }
            .sortedBy { it.createdAt }
            .toList()

        adapter.submit(filtered)
        btnActive.alpha = if (!showingDone) 1f else 0.7f
        btnDone.alpha = if (showingDone) 1f else 0.7f
    }

    private fun save() {
        val arr = JSONArray()
        all.forEach {
            arr.put(JSONObject().apply {
                put("id", it.id); put("title", it.title); put("category", it.category)
                put("color", it.color); put("done", it.done); put("createdAt", it.createdAt)
            })
        }
        prefs.edit().putString(keyTodos, arr.toString()).apply()
    }

    private fun load() {
        all.clear()
        val raw = prefs.getString(keyTodos, null) ?: return
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                all.add(
                    Todo(
                        id = o.optString("id"),
                        title = o.optString("title"),
                        category = o.optString("category"),
                        color = o.optInt("color"),
                        done = o.optBoolean("done", false),
                        createdAt = o.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) { /* ignore */ }
    }

    // --- adapter ---
    private class TodoDiff(private val old: List<Todo>, private val new: List<Todo>) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(o: Int, n: Int) = old[o].id == new[n].id
        override fun areContentsTheSame(o: Int, n: Int) = old[o] == new[n]
    }

    private inner class TodoAdapter(
        val onToggle: (Todo, Boolean) -> Unit,
        val onDelete: (Todo) -> Unit
    ) : RecyclerView.Adapter<TodoAdapter.TodoVH>() {

        private val items = mutableListOf<Todo>()
        fun submit(newList: List<Todo>) {
            val diff = DiffUtil.calculateDiff(TodoDiff(items, newList))
            items.clear(); items.addAll(newList); diff.dispatchUpdatesTo(this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoVH {
            val v = layoutInflater.inflate(R.layout.item_todo, parent, false)
            return TodoVH(v)
        }

        override fun onBindViewHolder(holder: TodoVH, position: Int) = holder.bind(items[position])
        override fun getItemCount() = items.size

        inner class TodoVH(v: View) : RecyclerView.ViewHolder(v) {
            private val card: MaterialCardView = v.findViewById(R.id.root)
            private val cb: CheckBox = v.findViewById(R.id.cbDone)
            private val title: TextView = v.findViewById(R.id.tvTitle)
            private val subtitle: TextView = v.findViewById(R.id.tvSubtitle)
            private val btnDelete: ImageButton = v.findViewById(R.id.btnDelete)

            fun bind(t: Todo) {
                title.text = t.title
                subtitle.text = t.category
                card.setCardBackgroundColor(t.color)

                cb.setOnCheckedChangeListener(null)
                cb.isChecked = t.done
                cb.setOnCheckedChangeListener { _, checked -> onToggle(t, checked) }

                val a = if (t.done) 0.5f else 1f
                title.alpha = a; subtitle.alpha = a; cb.alpha = a

                btnDelete.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.delete)
                        .setMessage(getString(R.string.delete_confirm))
                        .setPositiveButton(R.string.delete) { _, _ -> onDelete(t) }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }
        }
    }
}
