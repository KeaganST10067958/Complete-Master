package com.keagan.complete.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.keagan.complete.R
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

class HomeFragment : Fragment(R.layout.fragment_today) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // IDs that exist in fragment_today.xml
        val tvCount = view.findViewById<TextView>(R.id.tvCount)
        val tvUnit = view.findViewById<TextView>(R.id.tvUnit)
        val pbProgress = view.findViewById<ProgressBar>(R.id.pbProgress)
        val btnCheckIn = view.findViewById<MaterialButton>(R.id.btnCheckIn)
        val btnReset = view.findViewById<MaterialButton>(R.id.btnReset)
        val tvBest = view.findViewById<TextView>(R.id.tvBest)

        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        fun load(): Triple<Int, Int, String?> {
            val streak = prefs.getInt(KEY_STREAK, 0)
            val best = prefs.getInt(KEY_BEST, 0)
            val last = prefs.getString(KEY_LAST, null)
            return Triple(streak, best, last)
        }

        fun save(streak: Int, best: Int, last: String?) {
            prefs.edit()
                .putInt(KEY_STREAK, streak)
                .putInt(KEY_BEST, best)
                .putString(KEY_LAST, last)
                .apply()
        }

        fun refreshUI(streak: Int, best: Int, last: String?) {
            tvCount.text = streak.toString()
            tvUnit.text = if (streak == 1) "day streak" else "day streak"

            // progress toward 5 (just for the UI ring)
            val target = 5
            val pct = (min(streak, target) * 100) / target
            pbProgress.progress = pct

            tvBest.text = "Best: $best"

            val today = LocalDate.now().toString()
            val alreadyCheckedIn = (last == today)
            btnCheckIn.isEnabled = !alreadyCheckedIn
            btnCheckIn.text = if (alreadyCheckedIn) "Checked in" else "Check in"
        }

        // initial state
        val (streak0, best0, last0) = load()
        refreshUI(streak0, best0, last0)

        btnCheckIn.setOnClickListener {
            val today = LocalDate.now().toString()
            val yesterday = LocalDate.now().minusDays(1).toString()

            val (streak, best, last) = load()
            val newStreak = when (last) {
                today -> streak // safeguard; shouldn’t happen when button disabled
                yesterday -> streak + 1
                else -> 1
            }
            val newBest = max(best, newStreak)

            save(newStreak, newBest, today)
            refreshUI(newStreak, newBest, today)
        }

        btnReset.setOnClickListener {
            save(0, prefs.getInt(KEY_BEST, 0), null)
            val (s, b, l) = load()
            refreshUI(s, b, l)
        }
    }

    companion object {
        private const val PREFS = "streak_prefs"
        private const val KEY_STREAK = "streak"
        private const val KEY_BEST = "best"
        private const val KEY_LAST = "last_checkin"
    }
}
