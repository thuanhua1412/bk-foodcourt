package com.example.bkmerchant.storeActivity.store

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkmerchant.storeActivity.Store
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StoreViewModel : ViewModel() {
    private var firestore = FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser!!

    var openStoreDetailEvent = MutableLiveData<Store>()

    private var currentStoreId = ""

    init {
        firestore.collection("stores")
            .whereEqualTo("ownerID", currentUser.uid)
            .orderBy("isFocus", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { query ->
                if (!query.isEmpty) {
                    for (document in query) {
                        currentStoreId = document.id
                    }
                }
            }
    }

    fun setDefaultStore(id: String) {
        if (currentStoreId != id) {
            firestore.collection("stores")
                .document(currentStoreId)
                .update("isFocus", false)

            firestore.collection("stores")
                .document(id)
                .update("isFocus", true)

            currentStoreId = id
        }
    }

    fun openStore(store: Store) {
        openStoreDetailEvent.value = store
    }
}