package com.example.android.oshoppingapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.databinding.ActivityUserProfileBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.User
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader
import java.io.IOException


@Suppress("DEPRECATION")

class UserProfileActivity : BaseActivity(),View.OnClickListener {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails : User
    private var mSelectedImageFileUri:Uri? = null
    private var mUserPofileImageURL:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            //get user details from intent using parcelable extra
            mUserDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        binding.etFirstNAme.setText(mUserDetails.firstName)
        binding.etLastName.setText(mUserDetails.lastName)
        binding.etEmail.setText(mUserDetails.email)
        binding.etEmail.isEnabled=false


        if (mUserDetails.profileCompleted == 0) {
            // Update the title of the screen to complete profile.
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
            binding.etFirstNAme.isEnabled=false
            binding.etLastName.isEnabled=false
        } else {
            binding.etFirstNAme.isEnabled=true
            binding.etLastName.isEnabled=true
            // Call the setup action bar function.
            setupActionBar()
            // Update the title of the screen to edit profile.
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
            // Load the image using the GlideLoader class with the use of Glide Library.
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image
                , binding.ivUserPhoto)
            // Set the existing values to the UI and allow user to edit except the Email ID.
            if (mUserDetails.mobile != 0L) {
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                binding.rbMale.isChecked = true
            } else {
                binding.rbFemale.isChecked = true
            }
        }


        // Assign the on click event to the user profile photo.
        binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)

        binding.btnSubmit.setOnClickListener(this@UserProfileActivity)

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarUserProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {
                    // Here we will check if the permission is already allowed or we need to request for it.
                    // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed we will request for the same.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                        {
                            Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSIONS_CODE
                        )
                    }
                }

                R.id.btnSubmit ->{

                    if(validateUserProfileDetails()){
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (mSelectedImageFileUri!=null){
                            FireStoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri)
                        }else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }

    }

    private fun updateUserProfileDetails(){

        val userHashMap = HashMap<String, Any>()

        val firstName = binding.etFirstNAme.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = binding.etLastName.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mobileNumber.isNotEmpty()&& mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (mUserPofileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserPofileImageURL
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        // call the registerUser function of FireStore class to make an entry in the database.
        FireStoreClass().updateUserProfileData(
            this@UserProfileActivity, userHashMap)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSIONS_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,binding.ivUserPhoto)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess() {
        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        // Redirect to the Main Screen after profile completion.
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(imageURL: String) {

        mUserPofileImageURL=imageURL

        updateUserProfileDetails()
    }
}