package com.example.bk_foodcourt.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.DishItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DishAdapter(options: FirestoreRecyclerOptions<Dish>, val viewModel: MenuViewModel, val category: Category):
    FirestoreRecyclerAdapter<Dish, DishAdapter.DishViewHolder>(options) {

    class DishViewHolder private constructor(val binding: DishItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Dish, viewModel: MenuViewModel) {
            binding.dish = item
            binding.viewModel = viewModel
        }

        companion object {
            fun from(parent: ViewGroup): DishViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DishItemBinding.inflate(layoutInflater, parent, false)

                return DishViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        Log.d("DishAdapter", "Create dishviewholer")
        return DishViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int, item: Dish) {
        item.id = snapshots.getSnapshot(position).id
        item.categoryId = category.id
        item.storeId = category.storeId
        holder.bind(item, viewModel)
    }
}