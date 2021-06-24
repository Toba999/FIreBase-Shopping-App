 package com.example.android.oshoppingapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.databinding.ActivityLoginBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.User
import com.example.android.oshoppingapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth


@Suppress("DEPRECATION")
class LoginActivity :  BaseActivity(), View.OnClickListener {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val decorView: View = window.decorView
        val uiOptions: Int = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.setSystemUiVisibility(uiOptions)
        this.actionBar?.hide()



        // Click event assigned to Forgot Password text.
        binding.tvForgotPassword.setOnClickListener(this)
        // Click event assigned to Login button.
        binding.btnLogin.setOnClickListener(this)
        // Click event assigned to Register text.
        binding.tvRegistry.setOnClickListener(this)
        // END
    }


    fun userLoggedInSuccess(user:User){
        hideProgressDialog()

        if (user.profileCompleted == 0) {
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        } else {
            // Redirect the user to Main Screen after log in.
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btnLogin -> {

                    logInRegisteredUser()
                }

                R.id.tvRegistry -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegistryActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmailLogin.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPasswordLogin.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = binding.etEmailLogin.text.toString().trim { it <= ' ' }
            val password = binding.etPasswordLogin.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // Hide the progress dialog
                    if (task.isSuccessful) {
                        FireStoreClass().getUserDetails(this)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
}