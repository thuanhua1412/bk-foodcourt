package com.example.bkmerchant.order.orderDetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkmerchant.notificationService.Token
import com.example.bkmerchant.order.Order
import com.google.firebase.firestore.FirebaseFirestore

class OrderDetailViewModel(val order: Order): ViewModel() {
    private var firestore = FirebaseFirestore.getInstance()
    val sendNotificationEvent = MutableLiveData<String>()
    val navigateToOrderFragmentEvent = MutableLiveData<Boolean>()

    fun cancelOrder() {
        firestore.collection("tokens")
            .document(order.userID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val token = document.toObject(Token::class.java)!!
                    if (token.token.isNotEmpty()) {
                        sendNotificationEvent.value = token.token
                    }
                }
                firestore.collection("order")
                    .document(order.id)
                    .update("status", 4)
                    .addOnSuccessListener {
                        navigateToOrderFragmentEvent.value = true
                    }
            }
            .addOnFailureListener {
                Log.d("OrderDetailViewModel", it.toString())
            }
    }
}