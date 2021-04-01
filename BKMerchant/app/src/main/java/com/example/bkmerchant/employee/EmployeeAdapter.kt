package com.example.bkmerchant.employee

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bkmerchant.databinding.EmployeeItemBinding
import com.example.bkmerchant.login.AccountType
import com.example.bkmerchant.login.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeAdapter(options: FirestoreRecyclerOptions<User>):
    FirestoreRecyclerAdapter<User, EmployeeAdapter.EmployeeViewHolder>(options) {

    class EmployeeViewHolder private constructor(val binding: EmployeeItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User) {
            binding.user = item
        }

        companion object {
            fun from(parent: ViewGroup): EmployeeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = EmployeeItemBinding.inflate(layoutInflater, parent, false)

                return EmployeeViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        Log.d("EmployeeAdapter", "Create employee view holder")
        return EmployeeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int, item: User) {
        item.id = snapshots.getSnapshot(position).id
        holder.bind(item)
    }

    fun removePermission(position: Int) {
        val email = snapshots.getSnapshot(position).getString("email") ?: ""

        snapshots.getSnapshot(position).reference.update(
            "accountType", AccountType.CUSTOMER,
            "storeID", FieldValue.delete()
        )
        FirebaseFirestore.getInstance()
            .collection("userTypes")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                }
            }
    }
}