package com.example.android.oshoppingapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.databinding.ActivityProductDetailsBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.CartItem
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader

class ProductDetailsActivity :BaseActivity(),View.OnClickListener {

    private var mProductId: String = ""

     var mProductOwnerId: String = ""

    private lateinit var mProductDetails : Product

    private lateinit var binding: ActivityProductDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        if (FireStoreClass().getCurrentUserID()==mProductOwnerId){
            binding.btnAddToCart.visibility= View.GONE
        }else{
            binding.btnAddToCart.visibility= View.VISIBLE
        }

        setupActionBar()

        getProductDetails()

        binding.btnAddToCart.setOnClickListener(this)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FireStoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        mProductDetails = product
        // Hide Progress dialog.
        hideProgressDialog()
        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "$${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsStockQuantity.text = product.stock_quantity
    }

    override fun onClick(v: View?) {
        if (v!=null){
            when(v.id){
                R.id.btnAddToCart -> {
                    addToCart()
                }
            }
        }

    }


    private fun addToCart(){
        val cartItem=CartItem(
            FireStoreClass().getCurrentUserID(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addCartItems(this,cartItem)
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this, resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT).show()
    }

}