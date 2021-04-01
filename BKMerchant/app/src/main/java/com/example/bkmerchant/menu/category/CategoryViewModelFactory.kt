package com.example.bkmerchant.menu.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bkmerchant.menu.Category

@Suppress("UNCHECKED_CAST")
class CategoryViewModelFactory(private val category: Category): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(category) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}