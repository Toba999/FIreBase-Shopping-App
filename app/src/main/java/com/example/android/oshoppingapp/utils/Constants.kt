package com.example.android.oshoppingapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.android.oshoppingapp.utils.Constants.PICK_IMAGE_REQUEST_CODE

object Constants {
    const val USERS:String="users"
    const val MYSHOPPAL_PREFERENCES:String="MyShopPalPrefs"
    const val LOGGED_IN_USERNAME:String="logged_in_username"
    const val EXTRA_USER_DETAILS:String="extra_user_details"
    const val READ_STORAGE_PERMISSIONS_CODE=2
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val MALE : String = "male"
    const val FEMALE : String = "female"

    const val MOBILE : String = "mobile"
    const val GENDER : String = "gender"
    const val IMAGE : String = "image"
    const val USER_PROFILE_IMAGE : String = "User_Profile_Image"
    const val COMPLETE_PROFILE :String = "profileCompleted"


    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}

