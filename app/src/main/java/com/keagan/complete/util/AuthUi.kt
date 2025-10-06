package com.keagan.complete.util

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.keagan.complete.ui.MainActivity

object AuthUi {
    private const val TAG = "AuthCheck"

    fun showSuccessAndGoHome(activity: Activity, action: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d(TAG, "$action success. uid=$uid")
        Toast.makeText(activity, "$action success!", Toast.LENGTH_SHORT).show()
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }

    fun showFailure(activity: Activity, action: String, e: Exception?) {
        Log.e(TAG, "$action failed: ${e?.message}", e)
        Toast.makeText(activity, "$action failed: ${e?.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
