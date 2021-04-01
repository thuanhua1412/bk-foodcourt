package com.example.bk_foodcourt.menu

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @get: Exclude var id: String = "",
    @get: Exclude var storeId: String = "",
    var name: String = "",
    var priority: Int = 0
) : Parcelable

@Parcelize
data class Dish(
    @get: Exclude var id: String = "",
    @get: Exclude var quantity: Int = 0,
    @get: Exclude var options: String = "",
    @get: Exclude var cartItemId: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var discountPrice: Double = 0.0,
    var availability: Boolean = true,
    var imageUrl: String = "",
    @get: Exclude var categoryId: String = "",
    var storeId: String = ""
) : Parcelable