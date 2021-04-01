package com.example.bk_foodcourt.menu.dish

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bk_foodcourt.menu.CartItem
import com.example.bk_foodcourt.menu.Dish
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DishViewModel(val dish: Dish) : ViewModel() {
    companion object {
        const val TAG = "DishViewModel"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    var options = MutableLiveData<String>()
    var quantity = MutableLiveData<Int>()

    var navigateToMenuFragment = MutableLiveData<String>()
    var navigateToCartFragment = MutableLiveData<String>()

    init {
        bind()
    }

    private fun bind() {
        Log.d(TAG, dish.id)
        Log.d(TAG, dish.options)
        options.value = dish.options

        if (dish.quantity > 0) {
            quantity.value = dish.quantity
        } else {
            quantity.value = 1
        }
    }

    fun saveCart() {
        val dishQuantity = quantity.value!!
        val cartItem = CartItem(
            storeId = dish.storeId,
            name = dish.name,
            quantity = dishQuantity,
            total = dishQuantity * dish.discountPrice,
            baseTotal = dishQuantity * dish.price,
            options = options.value!!.trim(),
            description = dish.description,
            imageUrl = dish.imageUrl
        )

        if (dish.quantity == 0) {
            addToCart(cartItem)
        } else {
            updateCart(cartItem)
        }
    }

    private fun addToCart(item: CartItem) {
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("cart")
            .add(item)
            .addOnSuccessListener {
                navigateToMenuFragment.value = "Add ${item.name} successful"
            }
    }

    private fun updateCart(item: CartItem) {
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("cart")
            .document(dish.cartItemId)
            .set(item)
            .addOnSuccessListener {
                val message = "Update ${item.name} successful"
                navigateToMenuFragment.value = message
            }
    }

    fun increment() {
        quantity.value = quantity.value?.plus(1)
    }

    fun decrement() {
        if (quantity.value!! > 1) {
            quantity.value = quantity.value?.minus(1)
        }
    }
}