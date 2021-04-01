package com.example.bk_foodcourt.order.orderList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.OrderItemBinding
import com.example.bk_foodcourt.order.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class OrderAdapter(options: FirestoreRecyclerOptions<Order>, val viewModel: OrderViewModel):
    FirestoreRecyclerAdapter<Order, OrderAdapter.OrderViewHolder>(options) {

    class OrderViewHolder private constructor(private val binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Order, viewModel: OrderViewModel) {
            binding.order = item
            binding.viewModel = viewModel
        }

        companion object {
            fun from(parent: ViewGroup): OrderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = OrderItemBinding.inflate(layoutInflater, parent, false)

                return OrderViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        Log.d("OrderAdapter", "Create order view holder")
        return OrderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int, item: Order) {
        item.id = snapshots.getSnapshot(position).id
        holder.bind(item, viewModel)
    }
}