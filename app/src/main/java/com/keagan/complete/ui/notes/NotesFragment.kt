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
import com.keagan.complete.R
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

        // Separate buttons logic (replaces the old toggle-group)
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

    private fun showAddSheet() {
        // unchanged from your existing implementation
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
        )
        val v = layoutInflater.inflate(R.layout.bottom_sheet_add_note, null)
        dialog.setContentView(v)

        val inputTitle = v.findViewById<android.widget.EditText>(R.id.inputTitle)
        val inputText  = v.findViewById<android.widget.EditText>(R.id.inputText)
        val chipsColor = v.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipsColor)
        val preview    = v.findViewById<com.google.android.material.card.MaterialCardView>(R.id.previewCard)
        val btnCancel  = v.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        val btnSave    = v.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)

        requireNotNull(inputTitle); requireNotNull(inputText)
        requireNotNull(chipsColor); requireNotNull(preview)
        requireNotNull(btnCancel);  requireNotNull(btnSave)

        var chosen = com.keagan.complete.data.notes.NoteColor.PEACH
        fun applyPreview() {
            preview!!.setCardBackgroundColor(requireContext().getColor(chosen.toColorRes()))
        }
        fun hook(chipId: Int, c: com.keagan.complete.data.notes.NoteColor) {
            v.findViewById<com.google.android.material.chip.Chip>(chipId)
                .setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        chosen = c; applyPreview()
                        for (i in 0 until chipsColor!!.childCount) {
                            val child = chipsColor.getChildAt(i) as com.google.android.material.chip.Chip
                            if (child.id != chipId) child.isChecked = false
                        }
                    }
                }
        }
        hook(R.id.chipPeach, com.keagan.complete.data.notes.NoteColor.PEACH)
        hook(R.id.chipMint,  com.keagan.complete.data.notes.NoteColor.MINT)
        hook(R.id.chipBlue,  com.keagan.complete.data.notes.NoteColor.BLUE)
        hook(R.id.chipLav,   com.keagan.complete.data.notes.NoteColor.LAVENDER)
        hook(R.id.chipLemon, com.keagan.complete.data.notes.NoteColor.LEMON)
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

private fun com.keagan.complete.data.notes.NoteColor.toColorRes(): Int = when (this) {
    com.keagan.complete.data.notes.NoteColor.PEACH    -> R.color.peach_200
    com.keagan.complete.data.notes.NoteColor.MINT     -> R.color.mint_200
    com.keagan.complete.data.notes.NoteColor.BLUE     -> R.color.blue_200
    com.keagan.complete.data.notes.NoteColor.LAVENDER -> R.color.lav_200
    com.keagan.complete.data.notes.NoteColor.LEMON    -> R.color.lemon_200
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
