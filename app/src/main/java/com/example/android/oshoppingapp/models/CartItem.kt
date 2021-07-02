package com.example.android.oshoppingapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem (
    val user_id: String = "",
    var product_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    var stock_quantity: String = "",
    val image: String = "",
    var id :String = "",
):Parcelable