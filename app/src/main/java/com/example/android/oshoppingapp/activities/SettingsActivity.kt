package com.example.android.oshoppingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.databinding.ActivitySettingsBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User

class SettingsActivity : BaseActivity() , View.OnClickListener{

    private lateinit var mUserDetails: com.example.android.oshoppingapp.models.User


    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.tvEdit.setOnClickListener(this@SettingsActivity)

        binding.btnLogout.setOnClickListener ( this@SettingsActivity )

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSettingsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user:com.example.android.oshoppingapp.models.User){

        mUserDetails=user
        hideProgressDialog()

        GlideLoader(this).loadUserPicture(user.image,binding.ivUserPhoto)
        binding.tvName.text="${user.firstName} ${user.lastName}"
        binding.tvGender.text=user.gender
        binding.tvEmail.text=user.email
        binding.tvMobileNumber.text= "${ user.mobile }"

    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

            }
        }
    }
}