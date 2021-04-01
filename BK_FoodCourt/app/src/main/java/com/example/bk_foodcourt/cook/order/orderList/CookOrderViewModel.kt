package com.example.bk_foodcourt.cook.order.orderList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bk_foodcourt.order.Order

class CookOrderViewModel : ViewModel() {
    val showOrderDetailEvent = MutableLiveData<Order>()

    fun showOrderDetail(order: Order) {
        showOrderDetailEvent.value = order
    }
}