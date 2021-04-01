package com.example.bk_foodcourt.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.StoreListItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class StoreAdapter(options: FirestoreRecyclerOptions<Store>, private val viewModel: StoreViewModel):
    FirestoreRecyclerAdapter<Store, StoreAdapter.StoreViewHolder>(options) {

    class StoreViewHolder private constructor(private val binding: StoreListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Store, viewModel: StoreViewModel) {
            binding.store = item
            binding.viewModel = viewModel

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): StoreViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StoreListItemBinding.inflate(layoutInflater, parent, false)

                return StoreViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        return StoreViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int, item: Store) {
        item.id = snapshots.getSnapshot(position).id
        Log.d("StoreAdapter", item.name)
        holder.bind(item, viewModel)
    }
}