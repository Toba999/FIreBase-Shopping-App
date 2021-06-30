package com.example.android.oshoppingapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.oshoppingapp.activities.ProductDetailsActivity
import com.example.android.oshoppingapp.databinding.ItemListLayoutBinding
import com.example.android.oshoppingapp.fragments.ProductsFragment
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader

open class MyProductsListAdapter(

    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: ProductsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(model.image, holder.viewBinding.ivItemImage)
            holder.viewBinding.tvItemName.text = model.title
            holder.viewBinding.tvItemPrice.text = "$${model.price}"


            holder.viewBinding.ibDeleteProduct.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.viewBinding.ibEditProduct.setOnClickListener {
                fragment.editProduct(model.product_id)
            }

            holder.itemView.setOnClickListener {
                val intent=Intent(context,ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID,model.product_id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(var viewBinding: ItemListLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root)

}