package com.example.bk_foodcourt.menu

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Promotion(
    @get: Exclude var id: String = "",
    var label: String = "",
    var type: Int = 0,
    var scope: Int = 0,
    var value: Double = 0.0,
    var numUsed: Int = 0,
    var numAllowed: Int = 10000,
    var activateDay: Timestamp = Timestamp(0, 0),
    var expireDay: Timestamp = Timestamp(0, 0),
    var activateDayTime: Int = 0,
    var expireDayTime: Int = 1439,
    @field:JvmField var status: Boolean = true,

    var discountList: HashMap<String, String> = HashMap(),

    // for order discount only
    var code: String = "",
    var orderFrom: Double = 0.0,
    var orderTo: Double = 0.0,

    @get: Exclude var storeId: String=""
) : Parcelable