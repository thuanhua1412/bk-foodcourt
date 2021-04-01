package com.example.bk_foodcourt.cook.order.orderDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bk_foodcourt.order.Order

@Suppress("UNCHECKED_CAST")
class CookOrderDetailFactory(private val order: Order): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CookOrderDetailViewModel::class.java)) {
            return CookOrderDetailViewModel(order) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}