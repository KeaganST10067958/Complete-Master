package com.keagan.complete.ui.features

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.keagan.complete.R
import com.keagan.complete.databinding.FragmentSettingsBinding
import com.keagan.complete.ui.LoginActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val prefs by lazy {
        requireContext().getSharedPreferences("app_settings", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // --- Header name from Firebase user (fallbacks if missing)
        val user = auth.currentUser
        val displayName = user?.displayName
            ?: user?.email
            ?: getString(R.string.settings_default_user_name)
        binding.textUserName.text = displayName

        // --- Profile tile opens a basic info dialog
        binding.cardProfile.setOnClickListener {
            val email = user?.email ?: getString(R.string.settings_unknown)
            val uid = user?.uid ?: getString(R.string.settings_unknown)
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.settings_profile_title))
                .setMessage(getString(R.string.settings_profile_body, displayName, email, uid))
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }

        // --- Dark/Light mode switch (persisted)
        val switch = binding.switchTheme as SwitchMaterial
        val isDark = prefs.getBoolean("dark_mode", false)
        switch.isChecked = isDark
        applyNightMode(isDark, applyNow = false)

        switch.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("dark_mode", checked).apply()
            applyNightMode(checked, applyNow = true)
        }

        // --- Notifications tile -> “Coming soon”
        binding.cardNotifications.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.settings_notifications))
                .setMessage(getString(R.string.settings_notifications_soon))
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }

        // --- Logout button (proper sign out + clear task to LoginActivity)
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Snackbar.make(binding.root, R.string.settings_logged_out, Snackbar.LENGTH_SHORT).show()
            val i = Intent(requireContext(), LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(i)
            requireActivity().finish()
        }
    }

    private fun applyNightMode(dark: Boolean, applyNow: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        if (applyNow) {
            // Re-apply theme to this activity without disturbing navigation
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
