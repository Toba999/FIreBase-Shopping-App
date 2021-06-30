package com.example.android.oshoppingapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.oshoppingapp.databinding.ItemHomeLayoutBinding
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.GlideLoader

open class HomeItemsListAdapter(
        private val context: Context,
        private var list: ArrayList<Product>

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemHomeLayoutBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                    model.image,
                    holder.viewBinding.ivHomeItemImage
            )
            holder.viewBinding.tvHomeItemTitle.text = model.title
            holder.viewBinding.tvHomeItemPrice.text = "$${model.price}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }

    class MyViewHolder(var viewBinding: ItemHomeLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root)
}