package com.example.android.oshoppingapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.databinding.ActivityProductDetailsBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.CartItem
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader

class ProductDetailsActivity :BaseActivity(),View.OnClickListener {

    private var mProductId: String = ""

    private var mProductOwnerId: String = ""

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

        binding.btnGoToCart.setOnClickListener(this)

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
        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "$${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsStockQuantity.text = product.stock_quantity


        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE

            binding.tvProductDetailsStockQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            binding.tvProductDetailsStockQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (FireStoreClass().getCurrentUserID() == product.user_id) {
                hideProgressDialog()
            } else {
                FireStoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }

    }

    override fun onClick(v: View?) {
        if (v!=null){
            when(v.id){
                R.id.btnAddToCart -> {
                    addToCart()
                }
                R.id.btnGoToCart -> {
                    startActivity(Intent(this,CartListActivity::class.java))
                }
            }
        }

    }


    private fun addToCart(){
        val cartItem=CartItem(
            FireStoreClass().getCurrentUserID(),
            mProductOwnerId,
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

        binding.btnAddToCart.visibility= View.GONE
        binding.btnGoToCart.visibility= View.VISIBLE

    }

    fun productExistsInCart() {
        hideProgressDialog()

        binding.btnAddToCart.visibility= View.GONE
        binding.btnGoToCart.visibility= View.VISIBLE

    }

}