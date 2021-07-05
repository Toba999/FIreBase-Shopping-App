package com.example.android.oshoppingapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android.oshoppingapp.activities.AddEditAddressActivity
import com.example.android.oshoppingapp.activities.CheckoutActivity
import com.example.android.oshoppingapp.databinding.ItemAddressLayoutBinding
import com.example.android.oshoppingapp.databinding.ItemCartLayoutBinding
import com.example.android.oshoppingapp.models.Address
import com.example.android.oshoppingapp.utils.Constants

open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding =ItemAddressLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.viewBinding.tvAddressFullName.text = model.name
            holder.viewBinding.tvAddressType.text = model.type
            holder.viewBinding.tvAddressDetails.text = "${model.address}, ${model.zipCode}"
            holder.viewBinding.tvAddressMobileNumber.text = model.mobileNumber

            if (selectAddress) {
                holder.itemView.setOnClickListener {
                    val intent = Intent(context,CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS,model)
                    context.startActivity(intent)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    // Notify any registered observers that the item at position has changed.
    }


    private class MyViewHolder(var viewBinding: ItemAddressLayoutBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}
