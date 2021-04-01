package com.example.managertab

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddVendorViewModel//empty constructor needed
    () : ViewModel() {
    var name = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var website = MutableLiveData<String>()
    var hotline = MutableLiveData<String>()
    var supportEmail = MutableLiveData<String>()
    var imageUrl = ""
    var openTime = MutableLiveData<Int>()
    var closeTime = MutableLiveData<Int>()
}