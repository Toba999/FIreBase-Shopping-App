package com.example.android.oshoppingapp.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.databinding.ActivityEditProductBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader
import java.io.IOException

class EditProductActivity : BaseActivity(),View.OnClickListener {

    private var mSelectedImageFileUri: Uri? = null

    private var mProductImageURL: String = ""

    private var mProductId: String = ""

    private lateinit var binding: ActivityEditProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i(Constants.EXTRA_PRODUCT_ID, mProductId)
        }

        setupActionBar()

        getProductDetails()

        binding.ivAddUpdateProduct.setOnClickListener(this)

        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarEditProductActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarEditProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getProductDetails(this@EditProductActivity, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        // Hide Progress dialog.
        hideProgressDialog()
        // Populate the product details in the UI.
        GlideLoader(this).loadProductPicture(
                product.image,
                binding.ivProductImage
        )

        mProductImageURL=product.image

        binding.etProductTitle.setText(product.title)
        binding.etProductPrice.setText("$${product.price}")
        binding.etProductDescription.setText(product.description)
        binding.etProductQuantity.setText(product.stock_quantity)
    }

    override fun onClick(v: View?) {
        if(v !=null){
            when(v.id){
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE) }
                }
                R.id.btn_submit -> {
                    if (validateProductDetails()) {
                        if(mSelectedImageFileUri == null){
                            updateProductDetails()
                        }else{
                            uploadProductImage()
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray)
    { super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
                && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
                && data!!.data != null)
        { // Replace the add icon with edit icon once the image is selected.
            binding.ivAddUpdateProduct.setImageDrawable(
                    ContextCompat.getDrawable(this,
                            R.drawable.ic_vector_edit))
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!
            try { // Load the product image in the ImageView.
                GlideLoader(this).loadUserPicture(
                        mSelectedImageFileUri!!,
                        binding.ivProductImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {


            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_product_description),
                        true
                )
                false
            }

            TextUtils.isEmpty(binding.etProductQuantity?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                        resources.getString(R.string.err_msg_enter_product_quantity),
                        true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,
                Constants.PRODUCT_IMAGE)
    }

    fun imageUploadSuccess(imageURL: String) {
        // Initialize the global image url variable.
        mProductImageURL = imageURL
        updateProductDetails()
    }

    private fun updateProductDetails() {
        val productHashMap = HashMap<String, Any>()

        // Here we get the text from editText and trim the space

        val productTitle = binding.etProductTitle.text.toString().trim { it <= ' ' }
        productHashMap[Constants.TITLE] = productTitle

        val productPrice = binding.etProductPrice.text.toString().trim { it <= ' ' }
        productHashMap[Constants.PRICE] = productPrice

        val productDescription = binding.etProductDescription.text.toString().trim { it <= ' ' }
        productHashMap[Constants.DESCRIPTION] = productDescription

        val productQuantity = binding.etProductQuantity.text.toString().trim { it <= ' ' }
        productHashMap[Constants.STOCK_QUANTITY] = productQuantity

        productHashMap[Constants.IMAGE] =  mProductImageURL

        FireStoreClass().updateProductDetails(this,productHashMap,mProductId)
    }


    fun productUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.product_updated_success_message), Toast.LENGTH_SHORT).show()
        finish()
    }
}

