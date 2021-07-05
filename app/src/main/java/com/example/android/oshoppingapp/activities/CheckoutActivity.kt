package com.example.android.oshoppingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.adapters.CartItemsListAdapter
import com.example.android.oshoppingapp.databinding.ActivityCheckoutBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.Address
import com.example.android.oshoppingapp.models.CartItem
import com.example.android.oshoppingapp.models.Order
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants

class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding

    private var mAddressDetails : Address? =null
    // A global variable for the product list.
    private lateinit var mProductsList: ArrayList<Product>
    // A global variable for the cart list.
    private lateinit var mCartItemsList: ArrayList<CartItem>
    // A global variable for the SubTotal Amount.
    private var mSubTotal: Double = 0.0

    // A global variable for the Total Amount.
    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }

        getProductList()


        setupActionBar()

        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
       FireStoreClass().getAllProductsList(this)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        mProductsList = productsList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FireStoreClass().getCartList(this@CheckoutActivity)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        // Hide progress dialog.
        hideProgressDialog()
        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemsList = cartList

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter

        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }

       binding.tvCheckoutSubTotal.text = "$$mSubTotal"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
       binding.tvCheckoutShippingCharge.text = "$10.0"
        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 10.0
            binding.tvCheckoutTotalAmount.text = "$$mTotalAmount"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
    }

    private fun placeAnOrder() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        val order = Order(
            FireStoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "10.0", // The Shipping Charge is fixed as $10 for now in our case.
            mTotalAmount.toString(),
        )
        FireStoreClass().placeOrder(this@CheckoutActivity, order)
    }

    fun orderPlacedSuccess() {
        hideProgressDialog()
        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}