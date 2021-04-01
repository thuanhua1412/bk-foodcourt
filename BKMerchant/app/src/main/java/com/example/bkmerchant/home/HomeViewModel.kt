package com.example.bkmerchant.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkmerchant.storeActivity.Store
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var firestore = FirebaseFirestore.getInstance()

    var errorMessage = MutableLiveData<String>()
    var userName = MutableLiveData<String>()
    var currentStore = MutableLiveData(Store())

    init {
        getUserName()
    }

    private fun getUserName() {
        if (null != currentUser) {
            firestore.collection("users")
                .document(currentUser!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    userName.value = document.getString("name")
                }
        } else {
            userName.value = "Error occurred"
        }
    }

    fun getCurrentStore() {
        if (null != currentUser) {
            firestore.collection("stores")
                .whereEqualTo("ownerID", currentUser!!.uid)
                .orderBy("isFocus", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener {query ->
                    if (query.isEmpty) {
                        errorMessage.value = "You don't have any store!"
                    } else {
                        for (document in query) {
                            val store = document.toObject(Store::class.java)
                            store.id = document.id
                            currentStore.value = store
                            if (!store.isFocus) {
                                firestore.collection("stores")
                                    .document(document.id)
                                    .update("isFocus", true)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    errorMessage.value = it.toString()
                }
        }
    }
}