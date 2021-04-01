package com.example.bk_foodcourt

import android.widget.Button

class Row(s: String, s1: String) {
    private var orderID: String = ""
    private var userID: String = ""

    init {
        this.orderID = s
        this.userID = s1
    }
    public fun getOrderID(): String {
        return this.orderID
    }
    public fun getUserID(): String {
        return this.userID
    }
    public fun setOrderID(orderID: String) {
        this.orderID = orderID
    }
    public fun setUserID(userID: String) {
        this.userID = userID
    }
}