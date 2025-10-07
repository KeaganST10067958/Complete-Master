package com.keagan.complete.ui.features

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.keagan.complete.R
import com.keagan.complete.data.notes.NoteColor
import com.keagan.complete.data.todos.TodoCategory
import com.keagan.complete.databinding.FragmentPlaceholderTodosBinding
import com.keagan.complete.ui.todos.TodosAdapter
import com.keagan.complete.ui.todos.TodosViewModel
import kotlinx.coroutines.launch

class TodosFragment : Fragment(R.layout.fragment_placeholder_todos) {

    private var _binding: FragmentPlaceholderTodosBinding? = null
    private val binding get() = _binding!!
    private val vm by viewModels<TodosViewModel>()
    private lateinit var adapter: TodosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceholderTodosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // List
        adapter = TodosAdapter(
            onToggle = { t, checked -> vm.setDone(t, checked) },
            onDelete = { t -> vm.delete(t) }
        )
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TodosFragment.adapter
        }

        // Observe
        viewLifecycleOwner.lifecycleScope.launch {
            vm.todos.collect { items ->
                adapter.submitList(items)
                binding.empty.isVisible = items.isEmpty()
            }
        }

        // FAB -> add sheet
        binding.fabAdd.setOnClickListener { showAddSheet() }
    }

    private fun showAddSheet() {
        val v = layoutInflater.inflate(R.layout.bottom_sheet_add_task, null, false)

        // defaults
        v.findViewById<Chip>(R.id.chipWork)?.isChecked = true
        v.findViewById<Chip>(R.id.chipPeach)?.isChecked = true
        wireColorChips(v)

        val dialog = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
        )
        dialog.setContentView(v)

        v.findViewById<View>(R.id.btnCancel)?.setOnClickListener { dialog.dismiss() }
        v.findViewById<View>(R.id.btnSave)?.setOnClickListener {
            val title = v.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.inputTitle)
                ?.text?.toString()?.trim().orEmpty()
            val category = resolveCategory(v.findViewById(R.id.chipsCategory))
            val color = resolveColor(v.findViewById(R.id.chipsColor))

            if (title.isBlank()) {
                // simple inline error
                v.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.inputTitle)
                    ?.error = getString(R.string.required)
                return@setOnClickListener
            }

            vm.add(title, category, color)
            dialog.dismiss()
        }

        dialog.show()
    }

    // ---- helpers ----

    private fun resolveCategory(group: ChipGroup?): TodoCategory = when {
        group?.findViewById<Chip>(R.id.chipStudy)?.isChecked == true -> TodoCategory.STUDY
        group?.findViewById<Chip>(R.id.chipWork)?.isChecked == true -> TodoCategory.WORK
        group?.findViewById<Chip>(R.id.chipPersonal)?.isChecked == true -> TodoCategory.PERSONAL
        else -> TodoCategory.MISC
    }

    private fun resolveColor(group: ChipGroup?): NoteColor = when {
        group?.findViewById<Chip>(R.id.chipPeach)?.isChecked == true -> NoteColor.PEACH
        group?.findViewById<Chip>(R.id.chipMint)?.isChecked == true -> NoteColor.MINT
        group?.findViewById<Chip>(R.id.chipBlue)?.isChecked == true -> NoteColor.BLUE
        group?.findViewById<Chip>(R.id.chipLav)?.isChecked == true -> NoteColor.LAVENDER
        else -> NoteColor.LEMON
    }

    private fun Int.toStateList(): ColorStateList =
        ColorStateList.valueOf(ContextCompat.getColor(requireContext(), this))

    private fun wireColorChips(sheet: View) {
        val preview = sheet.findViewById<MaterialCardView>(R.id.previewCard)

        val chips = listOf(
            R.id.chipPeach to R.color.peach_200,
            R.id.chipMint to R.color.mint_200,
            R.id.chipBlue to R.color.blue_200,
            R.id.chipLav to R.color.lav_200,
            R.id.chipLemon to R.color.lemon_200
        )

        chips.forEach { (chipId, colorId) ->
            sheet.findViewById<Chip>(chipId)
                .setOnCheckedChangeListener { _, checked ->
                    if (checked) preview?.setCardBackgroundColor(colorId.toStateList())
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
