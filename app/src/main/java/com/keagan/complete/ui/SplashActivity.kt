package com.keagan.complete.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.keagan.complete.databinding.ActivitySplashBinding
import com.keagan.complete.ui.splash.SwooshWriterView

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kick off the swoosh/typing animation using a resilient shim.
        binding.swooshWriter.runSplash {
            // tiny pause to “breathe” before leaving
            binding.swooshWriter.postDelayed({
                smoothExitToLogin()
            }, 600)
        }
    }

    private fun smoothExitToLogin() {
        val card = binding.cardContainer
        val fade = AlphaAnimation(1f, 0f).apply {
            duration = 450
            fillAfter = true
        }
        card.startAnimation(fade)

        card.postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            // simple crossfade
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 460)
    }
}

/**
 * Tries to start the custom splash animation on SwooshWriterView without
 * knowing the exact function name. Supports:
 *   - start(), play(), begin(), startSequence()
 *   - and the same names that take a Runnable callback
 * If none found, it waits ~2.6s then calls onFinish.
 */
private fun SwooshWriterView.runSplash(onFinish: () -> Unit) {
    val cls = this::class.java

    // Try no-arg starters first
    val noArgNames = arrayOf("start", "play", "begin", "startSequence")
    for (name in noArgNames) {
        try {
            val m = cls.getMethod(name)
            m.invoke(this)
            // fallback duration if the view doesn’t provide a callback
            postDelayed({ onFinish() }, 2600)
            return
        } catch (_: Exception) { /* try next */ }
    }

    // Try starters that accept a Runnable callback
    val runnableNames = arrayOf("start", "play", "begin", "startSequence")
    for (name in runnableNames) {
        try {
            val m = cls.getMethod(name, Runnable::class.java)
            m.invoke(this, Runnable { onFinish() })
            return
        } catch (_: Exception) { /* try next */ }
    }

    // Last resort: just wait a bit and continue.
    postDelayed({ onFinish() }, 2600)
}
