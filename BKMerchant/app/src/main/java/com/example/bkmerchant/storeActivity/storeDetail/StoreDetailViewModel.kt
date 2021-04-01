package com.example.bkmerchant.storeActivity.storeDetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bkmerchant.storeActivity.Store
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class StoreDetailViewModel(val store: Store): ViewModel() {
    var name = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var website = MutableLiveData<String>()
    var hotline = MutableLiveData<String>()
    var supportEmail = MutableLiveData<String>()
    var imageUrl = ""
    var openTime = MutableLiveData<Int>()
    var closeTime = MutableLiveData<Int>()

    private var firestore = FirebaseFirestore.getInstance()

    var navigateToMenuFragment = MutableLiveData<Boolean>()

    init {
        bind()
    }

    private fun bind() {
        name.value = store.name
        description.value = store.description
        hotline.value = store.hotline
        website.value = store.website
        supportEmail.value = store.supportEmail
        imageUrl = store.imageUrl
        openTime.value = store.openTime
        closeTime.value = store.closeTime
    }

    fun updateStore(url: String) {
        if (url.isNotEmpty() && imageUrl.isNotEmpty()) {
            FirebaseStorage.getInstance()
                .getReferenceFromUrl(imageUrl)
                .delete()
        }

        val map = HashMap<String, Any>()

        map["name"] = name.value ?: ""
        map["description"] = description.value ?: ""
        map["hotline"] = hotline.value ?: ""
        map["website"] = website.value ?: ""
        map["supportEmail"] = supportEmail.value ?: ""
        map["openTime"] = openTime.value ?: 0
        map["closeTime"] = closeTime.value ?: 0

        if (url.isNotEmpty()) {
            map["imageUrl"] = url
        }

        firestore.collection("stores")
            .document(store.id)
            .update(map)
            .addOnSuccessListener {
                navigateToMenuFragment.value = true
            }
            .addOnFailureListener {
                Log.d("StoreDetailViewModel", it.toString())
            }
    }
}