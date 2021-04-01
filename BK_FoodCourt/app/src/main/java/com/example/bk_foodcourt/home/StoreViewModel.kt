package com.example.bk_foodcourt.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StoreViewModel: ViewModel() {
    var openStoreMenuEvent = MutableLiveData<Store>()

    fun openStore(store: Store) {
        openStoreMenuEvent.value = store
    }
}