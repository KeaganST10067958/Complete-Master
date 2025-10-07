package com.keagan.complete.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.keagan.complete.auth.LoginActivity
import com.keagan.complete.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    // ⏱️ Tweak these to control pacing
    private val TOTAL_SPLASH_MS = 3800L    // whole splash length (was ~2.6s)
    private val FADE_OUT_MS = 450L         // fade duration right before leaving

    private val ui = Handler(Looper.getMainLooper())
    private var exitPosted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The SwooshWriterView animates itself on attach. We just wait TOTAL_SPLASH_MS.
        ui.postDelayed({ smoothExitToLogin() }, TOTAL_SPLASH_MS)
    }

    private fun smoothExitToLogin() {
        if (isFinishing || isDestroyed || exitPosted) return
        exitPosted = true

        val card: View? = binding.cardContainer
        if (card != null) {
            val fade = AlphaAnimation(1f, 0f).apply {
                duration = FADE_OUT_MS
                fillAfter = true
            }
            card.startAnimation(fade)
            ui.postDelayed({ goToLogin() }, FADE_OUT_MS + 10)
        } else {
            goToLogin()
        }
    }

    private fun goToLogin() {
        if (isFinishing || isDestroyed) return
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ui.removeCallbacksAndMessages(null)
    }
}
