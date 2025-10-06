package com.keagan.complete.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.keagan.complete.R
import com.keagan.complete.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tileCalendar.setOnClickListener {
            findNavController().navigate(R.id.calendarFragment)
        }
        binding.tileTodos.setOnClickListener {
            findNavController().navigate(R.id.todosFragment)
        }
        binding.tileSticky.setOnClickListener {
            findNavController().navigate(R.id.notesFragment)
        }
        binding.tilePomodoro.setOnClickListener {
            findNavController().navigate(R.id.pomodoroFragment)
        }
        // bottom bar "Settings" already points to @id/navigation_settings in your nav graph
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
