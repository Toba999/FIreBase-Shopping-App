package com.example.android.oshoppingapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android.oshoppingapp.R
import com.example.android.oshoppingapp.activities.CartListActivity
import com.example.android.oshoppingapp.activities.ProductDetailsActivity
import com.example.android.oshoppingapp.databinding.ItemCartLayoutBinding
import com.example.android.oshoppingapp.databinding.ItemHomeLayoutBinding
import com.example.android.oshoppingapp.firestore.FireStoreClass
import com.example.android.oshoppingapp.models.CartItem
import com.example.android.oshoppingapp.models.Product
import com.example.android.oshoppingapp.utils.Constants
import com.example.android.oshoppingapp.utils.GlideLoader

open class CartItemsListAdapter (
    private val context: Context,
    private var list: ArrayList<CartItem>

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCartLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.viewBinding.ivCartItemImage
            )
            holder.viewBinding.tvCartItemTitle.text = model.title
            holder.viewBinding.tvCartItemPrice.text = "$${model.price}"
            holder.viewBinding.tvCartQuantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                holder.viewBinding.ibRemoveCartItem.visibility = View.GONE
                holder.viewBinding.ibAddCartItem.visibility = View.GONE

                holder.viewBinding.tvCartQuantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.viewBinding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            } else {
                holder.viewBinding.ibRemoveCartItem.visibility = View.VISIBLE
                holder.viewBinding.ibAddCartItem.visibility = View.VISIBLE

                holder.viewBinding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondaryText
                    )
                )
            }

            holder.viewBinding.ibRemoveCartItem.setOnClickListener {
                if (model.cart_quantity == "1") {
                    FireStoreClass().removeItemFromCart(context, model.id)
                } else {
                    val cartQuantity: Int = model.cart_quantity.toInt()
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                    FireStoreClass().updateMyCart(context, model.id, itemHashMap)
                }
            }

            holder.viewBinding.ibAddCartItem.setOnClickListener {

                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // Show the progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FireStoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(R.string.msg_for_available_stock),
                            true
                        )
                    }
                }

            }

            holder.viewBinding.ibDeleteCartItem.setOnClickListener {

                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }
                FireStoreClass().removeItemFromCart(context, model.id)
            }
        }

    }


    class MyViewHolder(var viewBinding: ItemCartLayoutBinding) :
        RecyclerView.ViewHolder(viewBinding.root)


    override fun getItemCount(): Int {
        return list.size
    }
}