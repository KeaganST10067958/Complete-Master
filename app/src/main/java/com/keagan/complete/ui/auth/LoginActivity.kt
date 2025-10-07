package com.keagan.complete.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.keagan.complete.R
import com.keagan.complete.databinding.ActivityLoginBinding
import com.keagan.complete.ui.RegisterActivity
import com.keagan.complete.ui.quote.QuoteSplashActivity

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

        // Email/password login
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
                    if (task.isSuccessful) goToMain()
                    else showSnack(task.exception?.localizedMessage ?: "Login failed")
                }
        }

        // Sign up
        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Google sign-in
        binding.btnGoogleLogin.setOnClickListener {
            launchGoogleSignIn()
        }

        // Forgot password
        binding.tvForgot.setOnClickListener {
            val typed = binding.etEmail.text?.toString()?.trim().orEmpty()
            if (typed.isBlank()) {
                promptForEmailAndSendReset()
            } else {
                sendResetEmail(typed)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // If user is already signed in (e.g., reopened app), skip login
        auth.currentUser?.let { goToMain() }
    }

    private fun launchGoogleSignIn() {
        // Requires default_web_client_id from google-services.json in strings.xml
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
                if (task.isSuccessful) goToMain()
                else showSnack(task.exception?.localizedMessage ?: "Google auth failed")
            }
    }

    private fun promptForEmailAndSendReset() {
        val til = TextInputLayout(this).apply { hint = "Email" }
        val et = TextInputEditText(til.context).apply {
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        til.addView(et)

        MaterialAlertDialogBuilder(this)
            .setTitle("Reset password")
            .setMessage("Enter the email you used to sign up.")
            .setView(til)
            .setPositiveButton("Send") { _, _ ->
                val email = et.text?.toString()?.trim().orEmpty()
                if (email.isNotBlank()) sendResetEmail(email)
                else showSnack("Please enter an email address.")
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun sendResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSnack("Password reset email sent to $email")
                } else {
                    val msg = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "No account exists for $email."
                        else -> task.exception?.localizedMessage ?: "Failed to send reset email."
                    }
                    showSnack(msg)
                }
            }
    }

    private fun goToMain() {
        startActivity(Intent(this, QuoteSplashActivity::class.java))
        finish()
    }

    private fun showSnack(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}
