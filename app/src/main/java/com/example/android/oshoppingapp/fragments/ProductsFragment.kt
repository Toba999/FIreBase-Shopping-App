package com.example.android.oshoppingapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.activities.AddProductActivity
import com.example.android.oshoppingapp.activities.CartListActivity
import com.example.android.oshoppingapp.activities.EditProductActivity
import com.example.android.oshoppingapp.activities.SettingsActivity
import com.example.android.oshoppingapp.adapters.MyProductsListAdapter
import com.example.android.oshoppingapp.databinding.FragmentProductsBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductsFragment : BaseFragment() {

    private lateinit var binding:FragmentProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProductsBinding.inflate(layoutInflater, container, false)
        binding.ivAddProduct.setOnClickListener {
            startActivity(Intent(requireActivity(), AddProductActivity::class.java))
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        getProductListFromFireStore()
    }
    private fun getProductListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FireStoreClass().getProductsList(this@ProductsFragment)
    }


    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()


        if (productsList.size > 0) {
            binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

            binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyProductItems.setHasFixedSize(true)

            // START
            val adapterProducts =
                MyProductsListAdapter(requireActivity(), productsList, this@ProductsFragment)
            // END
            binding.rvMyProductItems.adapter = adapterProducts
        } else {
            binding.rvMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }
    }

    fun deleteProduct(productID: String) {
        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {
        // Hide the progress dialog
        hideProgressDialog()
        Toast.makeText(
                requireActivity(),
                resources.getString(R.string.product_delete_success_message),
                Toast.LENGTH_SHORT).show()

        // Get the latest products list from cloud firestore.
        getProductListFromFireStore()
    }


    fun editProduct(productID: String) {
        val intent =Intent(requireActivity(),EditProductActivity::class.java)
        intent.putExtra(Constants.EXTRA_PRODUCT_ID,productID)
        startActivity(intent)

    }

    private fun showAlertDialogToDeleteProduct(productID: String) {
        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            // Call the function of Firestore class.
            FireStoreClass().deleteProduct(this@ProductsFragment, productID)
            // END
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}