package com.keagan.complete.ui.features

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.keagan.complete.R
import com.keagan.complete.databinding.FragmentPlaceholderTodosBinding

class TodosFragment : Fragment(R.layout.fragment_placeholder_todos) {

    private var _binding: FragmentPlaceholderTodosBinding? = null
    private val binding get() = _binding!!

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

        // FAB opens the add-task bottom sheet
        binding.fabAdd.setOnClickListener {
            showAddSheet()
        }
    }

    private fun showAddSheet() {
        val sheet = layoutInflater.inflate(R.layout.bottom_sheet_add_task, null, false)

        // optional: default selections
        sheet.findViewById<Chip>(R.id.chipWork).isChecked = true
        sheet.findViewById<Chip>(R.id.chipPeach).isChecked = true

        wireColorChips(sheet)

        val dialog = BottomSheetDialog(
            requireContext(),
            com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
        )
        dialog.setContentView(sheet)

        sheet.findViewById<View>(R.id.btnCancel).setOnClickListener { dialog.dismiss() }
        sheet.findViewById<View>(R.id.btnSave).setOnClickListener {
            // TODO: read values and save your task
            dialog.dismiss()
        }

        dialog.show()
    }

    // ---- helpers ----

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
                    if (checked) preview.setCardBackgroundColor(colorId.toStateList())
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
