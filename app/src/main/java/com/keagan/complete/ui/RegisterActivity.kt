package com.keagan.complete.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.keagan.complete.databinding.ActivityRegisterBinding
import com.keagan.complete.auth.LoginActivity


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) navigateToMain()
                    else showError(task2.exception?.localizedMessage ?: "Google sign-in failed")
                }
            } catch (e: Exception) {
                showError(e.localizedMessage ?: "Google sign-in cancelled")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Create account
        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim().orEmpty()
            val email = binding.etEmail.text?.toString()?.trim().orEmpty()
            val pass = binding.etPassword.text?.toString()?.trim().orEmpty()
            val confirm = binding.etConfirm.text?.toString()?.trim().orEmpty()

            if (name.isBlank() || email.isBlank() || pass.isBlank() || confirm.isBlank()) {
                showError("Please fill in all fields")
                return@setOnClickListener
            }
            if (pass != confirm) {
                showError("Passwords do not match")
                return@setOnClickListener
            }

            binding.btnCreateAccount.isEnabled = false
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                binding.btnCreateAccount.isEnabled = true
                if (task.isSuccessful) {
                    // Save display name
                    val profile = userProfileChangeRequest { displayName = name }
                    auth.currentUser?.updateProfile(profile)?.addOnCompleteListener {
                        navigateToMain()
                    } ?: navigateToMain()
                } else {
                    showError(task.exception?.localizedMessage ?: "Registration failed")
                }
            }
        }

        binding.btnGoogleRegister.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.keagan.complete.R.string.default_web_client_id))
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(this, gso)

            client.signOut().addOnCompleteListener {
                googleLauncher.launch(client.signInIntent)
            }
        }


        // Back to Login
        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showError(msg: String) {
        binding.tvError.text = msg
        binding.tvError.visibility = android.view.View.VISIBLE
    }
}
