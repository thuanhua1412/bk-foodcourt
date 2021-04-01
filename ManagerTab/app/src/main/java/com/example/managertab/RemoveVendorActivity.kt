package com.example.managertab

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_remove_vendor.*


class RemoveVendorActivity : AppCompatActivity(),StoreItemAdapter.StoreItemClickListener {
    private lateinit var firebaseFireStore : FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private val storeItemList = mutableListOf<StoreItem>()
    private val adapter = StoreItemAdapter(storeItemList,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_vendor)
            firebaseFireStore = FirebaseFirestore.getInstance()
        // Codes to get stores from firebase and store it into the list
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        firebaseFireStore.collection("stores").orderBy("name")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        val storeItem: StoreItem = document.toObject(StoreItem::class.java)
                        Log.d("STORENAME:","$storeItem.name" )
                        storeItemList.add(storeItem)
                    }
                    recycler_view.adapter = adapter
                    val dividerItemDecoration = DividerItemDecoration(
                        recycler_view.context,
                        LinearLayoutManager(this).orientation
                        )
                    recycler_view.addItemDecoration(dividerItemDecoration)

                }
            }
            .addOnCanceledListener {
                Toast.makeText(this, "Disconected. Please Check your Internet connection and try again",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to connect to Firebase.Check your connection", Toast.LENGTH_SHORT).show()
            }


    }

    override fun onStoreClicked(position: Int) {
        confirmRemoveDialog(position)
    }
    private fun confirmRemoveDialog(position: Int) {
        val newFragment: DialogFragment = MyAlertDialogFragment
            .newInstance(position)
        newFragment.show(supportFragmentManager, "dialog")
    }
    fun removeStore(position : Int){
        val storeToDel = firebaseFireStore.collection("stores")
            .whereEqualTo("name",storeItemList.elementAt(position).name)
            .whereEqualTo("imageUrl",storeItemList.elementAt(position).imageUrl)
            .whereEqualTo("closeTime",storeItemList.elementAt(position).closeTime)
        storeToDel.get()
            .addOnSuccessListener {
                it.forEach{
                    it.reference.delete()
                        .addOnSuccessListener {
                            storeItemList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            Toast.makeText(this, "The store is removed Successfully.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(this, "Something went wrong. Try delete this store later",
                                Toast.LENGTH_SHORT).show()
                        }
                }
            }

    }
    class MyAlertDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val pos = arguments!!.getInt("position")
            return AlertDialog.Builder(activity!!)
                .setTitle("CONFIRM DELETE STORE")
                .setMessage("Delete this store? Action is irreversible")
                .setPositiveButton("YES",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        (activity as RemoveVendorActivity?)
                            ?.removeStore(pos)
                    })
                .setNegativeButton("NO",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        dialog.dismiss()
                    }).create()
        }

        companion object {
            fun newInstance(position: Int): MyAlertDialogFragment {
                val frag = MyAlertDialogFragment()
                val args = Bundle()
                args.putInt("position", position)
                frag.arguments = args
                return frag
            }
        }
    }
}
