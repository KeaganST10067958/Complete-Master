package com.keagan.complete.ui.quote

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.keagan.complete.databinding.ActivityQuoteSplashBinding
import com.keagan.complete.ui.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuoteSplashActivity : ComponentActivity() {
    private lateinit var binding: ActivityQuoteSplashBinding
    private val vm: QuoteSplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuoteSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm.quote.observe(this) { q ->
            binding.tvQuote.text = "“${q.quote}”"
            val by = q.author?.takeIf { it.isNotBlank() }?.let { "— $it" } ?: ""
            binding.tvAuthor.text = by

            lifecycleScope.launch {
                delay(3500) // ~3–5s
                startActivity(Intent(this@QuoteSplashActivity, MainActivity::class.java))
                finish()
            }
        }
        vm.load()
    }
}
