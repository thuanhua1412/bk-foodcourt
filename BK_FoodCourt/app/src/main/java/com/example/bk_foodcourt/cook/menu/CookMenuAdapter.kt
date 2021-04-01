package com.example.bk_foodcourt.cook.menu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.CookMenuItemBinding
import com.example.bk_foodcourt.menu.Dish
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CookMenuAdapter (options: FirestoreRecyclerOptions<Dish>, val viewModel: CookMenuViewModel):
    FirestoreRecyclerAdapter<Dish, CookMenuAdapter.DishViewHolder>(options) {

    class DishViewHolder private constructor(val binding: CookMenuItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Dish, viewModel: CookMenuViewModel) {
            binding.dish = item
            binding.viewModel = viewModel
        }

        companion object {
            fun from(parent: ViewGroup): DishViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CookMenuItemBinding.inflate(layoutInflater, parent, false)

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
        item.categoryId = snapshots.getSnapshot(position).reference.path
        holder.bind(item, viewModel)
    }
}