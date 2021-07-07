package com.example.android.oshoppingapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.oshoppingapp.activities.SoldProductDetailsActivity
import com.example.android.oshoppingapp.databinding.ItemListLayoutBinding
import com.example.android.oshoppingapp.models.SoldProduct
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader

open class SoldProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<SoldProduct>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.viewBinding.ivItemImage
            )

            holder.viewBinding.tvItemName.text = model.title
            holder.viewBinding.tvItemPrice.text = "$${model.price}"

            holder.viewBinding.ibDeleteProduct.visibility = View.GONE
            holder.viewBinding.ibEditProduct.visibility = View.GONE


            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    class MyViewHolder(var viewBinding: ItemListLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root)
}