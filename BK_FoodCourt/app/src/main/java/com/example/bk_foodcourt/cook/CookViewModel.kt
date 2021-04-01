package com.example.bk_foodcourt.cook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CookViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    val storeId = MutableLiveData<String>()
    init {
        getStoreId()
    }

    private fun getStoreId() {
        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val id = document.getString("storeID")?: ""

                    storeId.value = id
                }
            }
    }

}