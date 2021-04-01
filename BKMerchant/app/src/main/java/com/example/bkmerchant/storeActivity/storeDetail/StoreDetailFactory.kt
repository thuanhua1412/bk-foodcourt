package com.example.bkmerchant.storeActivity.storeDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bkmerchant.menu.dish.DishViewModel
import com.example.bkmerchant.storeActivity.Store

@Suppress("UNCHECKED_CAST")
class StoreDetailFactory(private val store: Store): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreDetailViewModel::class.java)) {
            return StoreDetailViewModel(store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}