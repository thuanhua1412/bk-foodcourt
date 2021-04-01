package com.example.managertab

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_check_vendor.*
import kotlinx.android.synthetic.main.activity_remove_vendor.*

class CheckVendorInfosActivity : AppCompatActivity() {
    private lateinit var firebaseFireStore : FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private val storeList = mutableListOf<Store>()
    private val storeAdapter = StoreAdapter(storeList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_vendor)

        firebaseFireStore = FirebaseFirestore.getInstance()
        store_recycler_view.layoutManager = LinearLayoutManager(this)
        store_recycler_view.setHasFixedSize(true)
        firebaseFireStore.collection("stores").orderBy("name")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    for (document in it.result!!){
                        val store : Store = document.toObject(Store::class.java)
                        Log.d("STORENAME:","$store" )
                        storeList.add(store)
                    }
                    store_recycler_view.adapter = storeAdapter
                    val dividerItemDecoration = DividerItemDecoration(
                        store_recycler_view.context,
                        LinearLayoutManager(this).orientation
                    )
                    store_recycler_view.addItemDecoration(dividerItemDecoration)
                }
            }
            .addOnCanceledListener {
                Toast.makeText(this, "Disconnected. Check you network and try again", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to Connect to firebase. Please restart the app and try again",
                    Toast.LENGTH_SHORT).show()
            }
    }
}