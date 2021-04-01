package com.example.bk_foodcourt.menu.dish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bk_foodcourt.menu.Dish

@Suppress("UNCHECKED_CAST")
class DishViewModelFactory(private val dish: Dish): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            return DishViewModel(dish) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}