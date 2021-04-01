package com.example.bkmerchant.storeActivity

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
    @get: Exclude
    var id: String="",
    val name: String = "",
    val imageUrl: String = "",
    val hotline: String = "",
    val website: String = "",
    val supportEmail: String = "",
    val description: String = "",
    @field:JvmField
    val isFocus: Boolean = false,
    val openTime: Int = 0,
    val closeTime: Int = 0,
    val totalRatings: Int = 0,
    val starRating: Double = 0.0
): Parcelable