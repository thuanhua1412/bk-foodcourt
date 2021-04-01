package com.example.bkmerchant.order.orderList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkmerchant.order.Order

class OrderViewModel: ViewModel() {
    val showOrderDetailEvent = MutableLiveData<Order>()

    fun showOrderDetail(order: Order) {
        showOrderDetailEvent.value = order
    }
}