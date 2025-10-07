package com.keagan.complete.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.keagan.complete.R
import com.keagan.complete.data.notes.NoteColor
import com.keagan.complete.databinding.FragmentNotesBinding
import kotlinx.coroutines.launch

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<NotesViewModel>()
    private lateinit var adapter: NotesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotesListAdapter(
            onDelete = { vm.delete(it) },
            onPinToggle = { vm.togglePin(it) }
        )
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycler.adapter = adapter

        // attach swipe-to-delete with undo
        attachSwipeToDelete(binding.recycler)

        // Separate buttons logic
        binding.btnAll.isChecked = true
        binding.btnAll.setOnClickListener {
            binding.btnAll.isChecked = true
            binding.btnPinned.isChecked = false
            vm.setTab(NotesTab.ALL)
        }
        binding.btnPinned.setOnClickListener {
            binding.btnAll.isChecked = false
            binding.btnPinned.isChecked = true
            vm.setTab(NotesTab.PINNED)
        }

        // Search
        binding.inputSearch.setOnQueryTextChanged { text -> vm.setQuery(text ?: "") }

        // Add note
        binding.btnAdd.setOnClickListener { showAddSheet() }

        // Observe list
        viewLifecycleOwner.lifecycleScope.launch {
            vm.notes.collect { items ->
                adapter.submitList(items)
                binding.empty.isVisible = items.isEmpty()
            }
        }
    }

    private fun attachSwipeToDelete(rv: RecyclerView) {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return
                val note = adapter.currentList.getOrNull(pos) ?: return

                // remove
                vm.delete(note)

                // show undo
                Snackbar.make(binding.root, getString(R.string.note_deleted), Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        vm.restore(note)
                    }
                    .show()
            }
        })
        helper.attachToRecyclerView(rv)
    }

    private fun showAddSheet() {
        val dialog = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
        )
        val v = layoutInflater.inflate(R.layout.bottom_sheet_add_note, null)
        dialog.setContentView(v)

        val inputTitle = v.findViewById<android.widget.EditText>(R.id.inputTitle)
        val inputText  = v.findViewById<android.widget.EditText>(R.id.inputText)
        val chipsColor = v.findViewById<ChipGroup>(R.id.chipsColor)
        val preview    = v.findViewById<MaterialCardView>(R.id.previewCard)
        val btnCancel  = v.findViewById<MaterialButton>(R.id.btnCancel)
        val btnSave    = v.findViewById<MaterialButton>(R.id.btnSave)

        requireNotNull(inputTitle); requireNotNull(inputText)
        requireNotNull(chipsColor); requireNotNull(preview)
        requireNotNull(btnCancel);  requireNotNull(btnSave)

        var chosen = NoteColor.PEACH
        fun applyPreview() {
            preview!!.setCardBackgroundColor(requireContext().getColor(chosen.toColorRes()))
        }
        fun hook(chipId: Int, c: NoteColor) {
            v.findViewById<Chip>(chipId).setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chosen = c; applyPreview()
                    for (i in 0 until chipsColor!!.childCount) {
                        val child = chipsColor.getChildAt(i) as Chip
                        if (child.id != chipId) child.isChecked = false
                    }
                }
            }
        }
        hook(R.id.chipPeach, NoteColor.PEACH)
        hook(R.id.chipMint,  NoteColor.MINT)
        hook(R.id.chipBlue,  NoteColor.BLUE)
        hook(R.id.chipLav,   NoteColor.LAVENDER)
        hook(R.id.chipLemon, NoteColor.LEMON)
        applyPreview()

        btnCancel!!.setOnClickListener { dialog.dismiss() }
        btnSave!!.setOnClickListener {
            val title = inputTitle!!.text?.toString().orEmpty()
            val body  = inputText!!.text?.toString().orEmpty()
            if (title.isNotBlank() || body.isNotBlank()) {
                vm.add(title, body, chosen)
                dialog.dismiss()
            } else {
                inputTitle.error = getString(R.string.required)
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/* ---------- helpers ---------- */

private fun NoteColor.toColorRes(): Int = when (this) {
    NoteColor.PEACH    -> R.color.peach_200
    NoteColor.MINT     -> R.color.mint_200
    NoteColor.BLUE     -> R.color.blue_200
    NoteColor.LAVENDER -> R.color.lav_200
    NoteColor.LEMON    -> R.color.lemon_200
}

private inline fun TextView.setOnQueryTextChanged(
    crossinline onChange: (String?) -> Unit
) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onChange(s?.toString())
        }
    })
}
