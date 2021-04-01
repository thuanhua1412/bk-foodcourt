package com.example.bkmerchant.menu.dish

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkmerchant.R
import com.example.bkmerchant.menu.Category
import com.example.bkmerchant.menu.Dish
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class DishViewModel(val dish: Dish): ViewModel() {
    companion object {
        const val TAG = "DishViewModel"
    }

    private val firestore = FirebaseFirestore.getInstance()

    var name = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var price = MutableLiveData<String>()
    var discountPrice = MutableLiveData("")
    var imageUrl = ""
    var categoryIndex = 0

    var categories = mutableListOf<Category>()
    var catList = MutableLiveData<List<String>>()

    var currentCategoryId = ""
    var currentIndex = 0
    var currentName = ""

    val nameFieldError = MutableLiveData<Int>()
    var navigateToMenuFragment = MutableLiveData<Boolean>()

    init {
        if (dish.id.isNotEmpty()) {
            bind()
        }
        getCategoryList()
    }

    private fun bind() {
        Log.d(TAG, dish.id)
        name.value = dish.name
        description.value = dish.description
        price.value = dish.price.toString()
        imageUrl = dish.imageUrl
        currentCategoryId = dish.categoryId
        currentName = dish.name

        if (dish.discountPrice != dish.price) {
            discountPrice.value = dish.discountPrice.toString()
        }
    }

    fun saveDish(url: String) {
        if (url.isNotEmpty() && imageUrl.isNotEmpty()) {
            FirebaseStorage.getInstance()
                .getReferenceFromUrl(imageUrl)
                .delete()
        }
        if (url.isNotEmpty()) {
            dish.imageUrl = url
        }
        val dishDiscountPrice = discountPrice.value ?: ""

        dish.name = name.value ?: ""
        dish.description = description.value ?: ""
        dish.price = (price.value ?: "0").toDouble()
        dish.categoryId = categories[categoryIndex].id

        if (dishDiscountPrice.isEmpty()) {
            dish.discountPrice = dish.price
        } else {
            dish.discountPrice = dishDiscountPrice.toDouble()
            if (dish.discountPrice > dish.price) {
                dish.discountPrice = dish.price
            }
        }

        if (dish.id.isNotEmpty() && dish.name.trim() == currentName) {
            updateDish()
        } else {
            firestore.collectionGroup("items")
                .whereEqualTo("storeId", dish.storeId)
                .whereEqualTo("name", dish.name)
                .get()
                .addOnSuccessListener {querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        if (dish.id.isNotEmpty()) {
                            updateDish()
                        } else {
                            addDish()
                        }
                    } else {
                        nameFieldError.value = R.string.duplicate_item
                    }
                }
        }
    }

    private fun addDish() {
        firestore.collection("stores")
            .document(dish.storeId)
            .collection("categories")
            .document(dish.categoryId)
            .collection("items")
            .add(dish)
            .addOnSuccessListener {
                navigateToMenuFragment.value = true
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    private fun updateDish() {
        if (currentIndex != categoryIndex) {
            firestore.collection("stores")
                .document(dish.storeId)
                .collection("categories")
                .document(currentCategoryId)
                .collection("items")
                .document(dish.id)
                .delete()
        }

        if (dish.name.trim() == currentName) {
            firestore.collection("stores")
                .document(dish.storeId)
                .collection("promotions")
                .whereGreaterThan("discountList.${dish.id}", "")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        document.reference.update("discountList.${dish.id}", dish.name)
                    }
                }
        }

        firestore.collection("stores")
            .document(dish.storeId)
            .collection("categories")
            .document(dish.categoryId)
            .collection("items")
            .document(dish.id)
            .set(dish)
            .addOnSuccessListener {
                navigateToMenuFragment.value = true
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    private fun getCategoryList(){
        val returnList = mutableListOf<String>()
        firestore.collection("stores")
            .document(dish.storeId)
            .collection("categories")
            .orderBy("priority", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {querySnapshot ->
                if (querySnapshot != null) {
                    for (snapshot in querySnapshot) {
                        val item = snapshot.toObject(Category::class.java)
                        item.id = snapshot.id

                        if (item.id == dish.categoryId) {
                            currentIndex = categories.size
                            categoryIndex = currentIndex
                        }

                        categories.add(item)
                        returnList.add(item.name)
                        Log.d(TAG, returnList[returnList.size-1])
                    }
                    catList.value = returnList.toList()
                } else {
                    Log.d(TAG, "empty result")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }
}