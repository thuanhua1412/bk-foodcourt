package com.example.bk_foodcourt.cook.order.orderDetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.notificationService.Token
import com.example.bk_foodcourt.order.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CookOrderDetailViewModel(val order: Order): ViewModel() {
    private var firestore = FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser!!

    val sendCancelNotificationEvent = MutableLiveData<String>()
    val sendFinishNotificationEvent = MutableLiveData<String>()
    val navigateToOrderFragmentEvent = MutableLiveData<Boolean>()
    val message = MutableLiveData<Int>()
    val status = MutableLiveData<Int>()

    init {
        status.value = order.status
    }

    fun processOrder() {
        if (status.value == 0) {
            firestore.collection("order")
                .document(order.id)
                .get()
                .addOnSuccessListener {document ->
                    if (document.exists()) {
                        val state = document.getDouble("status")!!.toInt()
                        if (state > 0) {
                            message.value = R.string.processed_order
                            navigateToOrderFragmentEvent.value = true
                        } else {
                            firestore.collection("order")
                                .document(order.id)
                                .update("status", FieldValue.increment(1), "cookID", currentUser.uid)
                                .addOnSuccessListener {
                                    message.value = R.string.get_order_success
                                    status.value = status.value!!.plus(1)
                                }
                        }
                    }
                }
        } else {
            firestore.collection("order")
                .document(order.id)
                .update("status", FieldValue.increment(1))
                .addOnSuccessListener {
                    status.value = status.value!!.plus(1)
                    firestore.collection("tokens")
                        .document(order.userID)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val token = document.toObject(Token::class.java)!!
                                if (token.token.isNotEmpty()) {
                                    Log.d("CookOrderDetail", token.token)
                                    sendFinishNotificationEvent.value = token.token
                                }
                            } else {
                                navigateToOrderFragmentEvent.value = true
                            }
                        }
                }
        }
    }

    fun cancelOrder() {
        firestore.collection("tokens")
            .document(order.userID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val token = document.toObject(Token::class.java)!!
                    if (token.token.isNotEmpty()) {
                        Log.d("CookOrderDetail", token.token)
                        sendCancelNotificationEvent.value = token.token
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