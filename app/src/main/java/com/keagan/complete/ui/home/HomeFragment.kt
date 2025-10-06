package com.keagan.complete.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.keagan.complete.R
import com.keagan.complete.data.placeholder.QuoteProvider
import java.time.LocalDate

class HomeFragment : Fragment(R.layout.fragment_today) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvQuote = view.findViewById<TextView>(R.id.tvQuote)
        val tvAuthor = view.findViewById<TextView>(R.id.tvAuthor)
        val tvStreakCount = view.findViewById<TextView>(R.id.tvStreakCount)
        val btnCheckIn = view.findViewById<MaterialButton>(R.id.btnCheckIn)

        // Quote (no refresh button)
        val (quote, author) = QuoteProvider.getTodaysQuote(requireContext())
        tvQuote.text = "“$quote”"
        tvAuthor.text = "— $author"

        // Streak (local placeholder)
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val today = LocalDate.now().toString()
        val lastCheckIn = prefs.getString(KEY_LAST_CHECKIN, null)
        val currentStreak = prefs.getInt(KEY_STREAK, 0)

        tvStreakCount.text = currentStreak.toString()

        val alreadyCheckedInToday = (lastCheckIn == today)
        btnCheckIn.isEnabled = !alreadyCheckedInToday
        if (alreadyCheckedInToday) btnCheckIn.text = getString(R.string.checked_in_today)

        btnCheckIn.setOnClickListener {
            val newStreak = if (lastCheckIn == null) {
                1
            } else {
                val yesterday = LocalDate.now().minusDays(1).toString()
                if (lastCheckIn == yesterday) currentStreak + 1 else 1
            }
            prefs.edit().putInt(KEY_STREAK, newStreak)
                .putString(KEY_LAST_CHECKIN, today)
                .apply()

            tvStreakCount.text = newStreak.toString()
            btnCheckIn.isEnabled = false
            btnCheckIn.text = getString(R.string.checked_in_today)
        }
    }

    companion object {
        private const val PREFS = "today_prefs"
        private const val KEY_STREAK = "streak_count"
        private const val KEY_LAST_CHECKIN = "last_checkin"
    }
}
