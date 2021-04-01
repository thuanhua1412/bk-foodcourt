package com.example.bk_foodcourt.order.orderDetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.OrderDetailItemBinding
import com.example.bk_foodcourt.order.OrderItem
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class OrderItemAdapter(options: FirestoreRecyclerOptions<OrderItem>):
    FirestoreRecyclerAdapter<OrderItem, OrderItemAdapter.OrderItemViewHolder>(options) {

    class OrderItemViewHolder private constructor(val binding: OrderDetailItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {
            binding.orderItem = item
        }

        companion object {
            fun from(parent: ViewGroup): OrderItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = OrderDetailItemBinding.inflate(layoutInflater, parent, false)

                return OrderItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        Log.d("OrderItemAdapter", "Create order view holder")
        return OrderItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int, item: OrderItem) {
        item.id = snapshots.getSnapshot(position).id
        holder.bind(item)
    }
}