package com.example.bk_foodcourt.menu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.DateFormat
import java.util.*

class MenuViewModel(private val storeId: String) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser!!
    val categories = MutableLiveData<List<String>>()

    val promotionList = MutableLiveData<List<Promotion>>()

    val isEmptyCart = MutableLiveData<Boolean>()

    companion object {
        const val TAG = "MenuViewModel"
    }

    init {
        Log.d("MenuViewModel", "Create view model")
        getCategoryList()
        getPromotionList()
    }

    val loadDiscountDone = MutableLiveData<Boolean>()
    val openDishEvent = MutableLiveData<Dish>()

    fun checkEmptyCart() {
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("cart")
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                isEmptyCart.value = querySnapshot.isEmpty
            }
    }

    fun openDish(dish: Dish) {
        openDishEvent.value = dish
    }

    private fun getCategoryList() {
        firestore.collection("stores")
            .document(storeId)
            .collection("categories")
            .orderBy("priority", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.d(TAG, exception.toString())
                } else if (querySnapshot != null) {
                    val list = mutableListOf<String>()
                    for (document in querySnapshot) {
                        val name = document.getString("name") ?: ""
                        list.add(name)
                    }
                    categories.value = list
                }
            }
    }

    private fun getPromotionList() {
        val list = mutableListOf<Promotion>()
        val todayTimestamp = Timestamp.now()
        firestore.collection("stores")
            .document(storeId)
            .collection("promotions")
            .whereEqualTo("status", true)
            .whereEqualTo("scope", 0)
            .whereLessThanOrEqualTo("activateDay", todayTimestamp)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val promotion = document.toObject(Promotion::class.java)
                    promotion.id = document.id
                    list.add(promotion)
                }
                promotionList.value = list
            }
    }
}