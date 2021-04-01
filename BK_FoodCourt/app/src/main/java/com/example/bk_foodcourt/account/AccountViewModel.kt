package com.example.bk_foodcourt.account

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bk_foodcourt.login.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AccountViewModel : ViewModel() {
    private var currentUser = FirebaseAuth.getInstance().currentUser!!
    private var firestore = FirebaseFirestore.getInstance()
    var user = MutableLiveData(User())

    var navigateToPersonalInfoEvent = MutableLiveData<User>()

    init {
        getUserInformation()
    }

    private fun getUserInformation() {
        firestore.collection("users")
            .document(currentUser.uid)
            .addSnapshotListener { document, exception ->
                if (exception == null) {
                    if (document != null) {
                        user.value = document.toObject(User::class.java)
                        user.value!!.id = document.id
                    }
                } else {
                    Log.d("AccountViewModel", exception.toString())
                }
            }
    }

    fun updateProfileImage(url: String) {
        val currentAvatar = user.value?.avatarUrl ?: ""
        if (currentAvatar.isNotEmpty()) {
            FirebaseStorage.getInstance()
                .getReferenceFromUrl(currentAvatar)
                .delete()
        }
        firestore.collection("users")
            .document(user.value!!.id)
            .update("avatarUrl", url)
    }

    fun navigateToPersonalInfo() {
        navigateToPersonalInfoEvent.value = user.value
    }
}