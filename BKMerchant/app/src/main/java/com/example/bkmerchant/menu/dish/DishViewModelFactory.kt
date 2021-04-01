package com.example.bkmerchant.menu.dish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bkmerchant.menu.Dish

@Suppress("UNCHECKED_CAST")
class DishViewModelFactory(private val dish: Dish): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            return DishViewModel(dish) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}