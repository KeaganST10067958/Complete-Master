package com.keagan.complete.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.keagan.complete.databinding.ActivityLoginBinding
import com.keagan.complete.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.keagan.complete.R

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrEmpty()) {
                showSnack("Google sign-in failed: empty idToken")
                return@registerForActivityResult
            }
            firebaseAuthWithGoogle(idToken)
        } catch (e: Exception) {
            Log.e("LoginActivity", "Google sign-in failed", e)
            showSnack("Google sign-in failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Email/password login (keep whatever you already had, if any)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.etPassword.text?.toString()?.trim().orEmpty()
            if (email.isEmpty() || pass.isEmpty()) {
                showSnack("Please enter email and password")
                return@setOnClickListener
            }
            binding.btnLogin.isEnabled = false
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    binding.btnLogin.isEnabled = true
                    if (task.isSuccessful) goToMain() else showSnack(task.exception?.localizedMessage ?: "Login failed")
                }
        }

        // --- Sign Up text
        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, com.keagan.complete.ui.RegisterActivity::class.java))
        }

        // --- Google button
        binding.btnGoogleLogin.setOnClickListener {
            launchGoogleSignIn()
        }
    }

    private fun launchGoogleSignIn() {
        // Requires default_web_client_id in strings.xml (from google-services.json)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, gso)
        googleLauncher.launch(client.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToMain()
                } else {
                    showSnack(task.exception?.localizedMessage ?: "Google auth failed")
                }
            }
    }

    private fun goToMain() {
        startActivity(Intent(this, com.keagan.complete.ui.quote.QuoteSplashActivity::class.java))
        finish()
    }


    private fun showSnack(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }
}
