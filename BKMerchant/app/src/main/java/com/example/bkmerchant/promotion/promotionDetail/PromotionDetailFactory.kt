package com.example.bkmerchant.promotion.promotionDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bkmerchant.promotion.Promotion

@Suppress("UNCHECKED_CAST")
class PromotionDetailFactory(val promotion: Promotion): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromotionDetailViewModel::class.java)) {
            return PromotionDetailViewModel(promotion) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}