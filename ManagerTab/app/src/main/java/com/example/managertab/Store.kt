package com.example.managertab

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
    data class Store(
        var id: String = "",
        var name: String = "",
        var imageUrl: String = "",
        var hotline: String = "",
        var website: String = "",
        var supportEmail: String = "",
        var description: String = "",
        var ownerName: String = "",
        var isFocus: Boolean = false,
        var openTime: Int = 0,
        var closeTime: Int = 0
    )
