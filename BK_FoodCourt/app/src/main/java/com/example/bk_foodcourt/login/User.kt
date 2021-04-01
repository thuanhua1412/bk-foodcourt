package com.example.bk_foodcourt.login

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @get: Exclude var id: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val email: String = "",
    var storeID: String = ""
): Parcelable

data class UserType(
    val email: String = "",
    val accountType: String = "CUSTOMER"
)