package com.example.android.oshoppingapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.activities.CartListActivity
import com.example.android.oshoppingapp.activities.DashboardActivity
import com.example.android.oshoppingapp.activities.ProductDetailsActivity
import com.example.android.oshoppingapp.adapters.HomeItemsListAdapter
import com.example.android.oshoppingapp.databinding.FragmentHomeBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants


class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(layoutInflater, container, false)

        binding.ivCart.setOnClickListener {
            startActivity(Intent(requireActivity(), CartListActivity::class.java))
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getHomeItemsList()
    }


    private fun getHomeItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getHomeItemsList(this@HomeFragment)
    }


    fun successHomeItemsList(HomeItemsList: ArrayList<Product>) {

        // Hide the progress dialog.
        hideProgressDialog()

        if (HomeItemsList.size > 0) {

            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE

            binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvDashboardItems.setHasFixedSize(true)

            val adapter = HomeItemsListAdapter(requireActivity(), HomeItemsList)
            binding.rvDashboardItems.adapter = adapter

            adapter.setOnClickListener(object :
                HomeItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,product.user_id)
                    startActivity(intent)
                }
            })

        } else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }
    }

}