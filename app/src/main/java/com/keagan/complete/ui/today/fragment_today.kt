package com.keagan.complete.ui.today

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.keagan.complete.R
import com.keagan.complete.databinding.FragmentTodayBinding
import java.time.LocalDate

class TodayFragment : Fragment(R.layout.fragment_today) {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTodayBinding.bind(view)

        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // Load state
        val today = LocalDate.now().toString()
        val lastCheckIn = prefs.getString(KEY_LAST, null)
        val current = prefs.getInt(KEY_STREAK, 0)
        val best = prefs.getInt(KEY_BEST, 0)

        // Initial UI
        updateUi(current, best, alreadyCheckedIn = (lastCheckIn == today))

        // Check-in
        binding.btnCheckIn.setOnClickListener {
            val oldLast = prefs.getString(KEY_LAST, null)
            val yesterday = LocalDate.now().minusDays(1).toString()

            val newStreak = when {
                oldLast == null -> 1
                oldLast == today -> current // guard, but button should be disabled
                oldLast == yesterday -> current + 1
                else -> 1
            }

            val newBest = maxOf(best, newStreak)

            prefs.edit()
                .putInt(KEY_STREAK, newStreak)
                .putInt(KEY_BEST, newBest)
                .putString(KEY_LAST, today)
                .apply()

            updateUi(newStreak, newBest, alreadyCheckedIn = true)
        }

        // Reset (keeps best; only clears current & last)
        binding.btnReset.setOnClickListener {
            prefs.edit()
                .putInt(KEY_STREAK, 0)
                .remove(KEY_LAST)
                .apply()

            val keepBest = prefs.getInt(KEY_BEST, 0)
            updateUi(0, keepBest, alreadyCheckedIn = false)
        }
    }

    private fun updateUi(streak: Int, best: Int, alreadyCheckedIn: Boolean) {
        // Main count
        binding.tvCount.text = streak.toString()
        binding.tvUnit.text = if (streak == 1) "day streak" else "day streak"

        // Best chip
        binding.tvBest.text = "Best: $best"

        // Milestone / progress
        val target = nextMilestone(streak)
        val remaining = (target - streak).coerceAtLeast(0)
        binding.tvMilestone.text = if (streak >= target) {
            "Milestone reached!"
        } else {
            "$remaining more day${if (remaining == 1) "" else "s"} to hit $target!"
        }

        binding.pbProgress.max = target
        binding.pbProgress.progress = streak.coerceAtMost(target)

        // Button state/text (keeps text visible & constant width)
        if (alreadyCheckedIn) {
            binding.btnCheckIn.isEnabled = false
            binding.btnCheckIn.text = "Checked in today"
        } else {
            binding.btnCheckIn.isEnabled = true
            binding.btnCheckIn.text = "Check in"
        }
    }

    private fun nextMilestone(streak: Int): Int {
        val milestones = listOf(1, 3, 5, 7, 14, 30, 50, 100)
        return milestones.firstOrNull { streak < it } ?: ( (streak / 50 + 1) * 50 )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PREFS = "today_prefs"
        private const val KEY_STREAK = "streak_count"
        private const val KEY_LAST = "last_checkin"
        private const val KEY_BEST = "best_streak"
    }
}
