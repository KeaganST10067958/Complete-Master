package com.keagan.complete.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.keagan.complete.auth.LoginActivity
import com.keagan.complete.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Try to start any animation if the view exists; otherwise just wait briefly.
        val swoosh = runCatching { binding.root.findViewById<View>(resources.getIdentifier("swooshWriter", "id", packageName)) }.getOrNull()

        // Fire and forget: never crash if the custom view/class is missing.
        if (swoosh != null) {
            // If your custom view has any of these methods, call them via reflection (safe).
            runCatching {
                val m = swoosh::class.java.methods.firstOrNull { it.name in setOf("start", "play", "begin", "startSequence") && it.parameterTypes.isEmpty() }
                if (m != null) {
                    m.invoke(swoosh)
                    swoosh.postDelayed({ smoothExitToLogin() }, 2600)
                } else {
                    // no starter method; just delay and go
                    swoosh.postDelayed({ smoothExitToLogin() }, 1600)
                }
            }.onFailure {
                // Even if reflection fails, still navigate
                swoosh.postDelayed({ smoothExitToLogin() }, 1600)
            }
        } else {
            // No animation view in this layout – simple timed splash
            binding.root.postDelayed({ smoothExitToLogin() }, 1000)
        }
    }

    private fun smoothExitToLogin() {
        if (isFinishing || isDestroyed) return

        // If your layout has a card container, fade it; otherwise just navigate.
        val cardId = resources.getIdentifier("cardContainer", "id", packageName)
        val card: View? = if (cardId != 0) findViewById(cardId) else null

        card?.let {
            val fade = AlphaAnimation(1f, 0f).apply {
                duration = 350
                fillAfter = true
            }
            it.startAnimation(fade)
            it.postDelayed({ goToLogin() }, 360)
        } ?: goToLogin()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
