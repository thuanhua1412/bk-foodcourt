package com.example.bkmerchant.promotion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PromotionViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    val showPromotionDetailEvent = MutableLiveData<Promotion>()

    fun changePromotionStatus(promotion: Promotion) {
        firestore.collection("stores")
            .document(promotion.storeId)
            .collection("promotions")
            .document(promotion.id)
            .update("status", !promotion.status)
    }

    fun showPromotionDetail(promotion: Promotion) {
        showPromotionDetailEvent.value = promotion
    }
}