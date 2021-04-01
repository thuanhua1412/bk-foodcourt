package com.example.bk_foodcourt.cook.order.orderList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.CookOrderItemBinding
import com.example.bk_foodcourt.order.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CookOrderAdapter(options: FirestoreRecyclerOptions<Order>, val viewModel: CookOrderViewModel):
    FirestoreRecyclerAdapter<Order, CookOrderAdapter.OrderViewHolder>(options) {

    class OrderViewHolder private constructor(private val binding: CookOrderItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Order, viewModel: CookOrderViewModel) {
            binding.order = item
            binding.viewModel = viewModel
        }

        companion object {
            fun from(parent: ViewGroup): OrderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CookOrderItemBinding.inflate(layoutInflater, parent, false)

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