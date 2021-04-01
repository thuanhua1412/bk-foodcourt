package com.example.bk_foodcourt.menu.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.CartItemBinding
import com.example.bk_foodcourt.menu.CartItem
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartItemAdapter(val viewModel: CartViewModel) :
    ListAdapter<CartItem, CartItemAdapter.CartItemViewHolder>(CartItemDiffCallback()) {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    class CartItemViewHolder private constructor(val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem, viewModel: CartViewModel) {
            binding.cartItem = item
            binding.viewModel = viewModel
        }

        companion object {
            fun from(parent: ViewGroup): CartItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CartItemBinding.inflate(layoutInflater, parent, false)

                return CartItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder.from(parent)
    }

    fun removeCartItem(position: Int) {
        val item = getItem(position)
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("cart")
            .document(item.id)
            .delete()
    }
}

class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {

    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}