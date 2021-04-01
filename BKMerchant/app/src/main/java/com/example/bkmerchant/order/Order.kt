package com.example.bkmerchant.order

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    @get: Exclude
    var id: String = "",
    var userID: String = "",
    var userName: String = "",
    var userPhoneNumber: String = "",
    var status: Int = 0,
    var time: Timestamp = Timestamp(0L, 0),
    var type: String = "",
    var applicableFee: Double = 0.0,
    var total: Double = 0.0,
    var promotionCode: String = "",
    var promotion: Double = 0.0
) : Parcelable

data class OrderItem(
    @get: Exclude
    var id: String = "",
    @get: Exclude
    var orderId: String = "",
    var name: String = "",
    var options: String = "",
    var total: Double = 0.0,
    var quantity: Int = 0
)

enum class OrderStatus(var value: Int) {
    CONFIRMED(0),
    PROCESSING(1),
    DONE_PROCESSING(2),
    FINISH(3),
    CANCEL(4)
}