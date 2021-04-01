package com.example.bk_foodcourt.cook.menu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bk_foodcourt.menu.Dish
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId

class CookMenuViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    val storeId = MutableLiveData<String>()
    val logoutEvent = MutableLiveData<Boolean>()

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
                    Log.d("CookViewModel", id)
                    storeId.value = id
                }
            }
    }

    fun logout() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { result ->
                Log.d("LoginFragment", "Current token: ${result.token}")
                firestore.collection("tokens")
                    .document(currentUser.uid)
                    .update("token", "")
                    .addOnSuccessListener {
                        Log.d("LoginFragment", "Update token successful")
                    }
            }
    }

    fun changeDishStatus(dish: Dish) {
        firestore.document(dish.categoryId)
            .update("availability", !dish.availability)
    }

}