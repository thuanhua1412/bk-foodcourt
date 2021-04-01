package com.example.bk_foodcourt.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bk_foodcourt.menu.dish.DishViewModel

class MenuViewModelFactory(private val storeId: String): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(storeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}