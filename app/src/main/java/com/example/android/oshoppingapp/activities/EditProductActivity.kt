package com.example.android.oshoppingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.databinding.ActivityEditProductBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader

class EditProductActivity : BaseActivity() {

    private var mProductId: String = ""
    private lateinit var binding: ActivityEditProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                    intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }

        setupActionBar()

        getProductDetails()
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

        // Call the function of FirestoreClass to get the product details.
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

        binding.etProductTitle.text.toString() = product.title
        binding.etProductPrice.text = "$${product.price}"
        binding.etProductDescription.text = product.description
        binding.etProductQuantity.text = product.stock_quantity
    }
}

