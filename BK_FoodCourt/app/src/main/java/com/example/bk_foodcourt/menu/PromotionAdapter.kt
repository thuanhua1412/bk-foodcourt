package com.example.bk_foodcourt.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.databinding.PromotionItemBinding

class PromotionAdapter(val viewModel: MenuViewModel) :
    ListAdapter<Promotion, PromotionAdapter.PromotionItemViewHolder>(PromotionItemDiffCallback()) {
    class PromotionItemViewHolder private constructor(val binding: PromotionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Promotion, viewModel: MenuViewModel) {
            binding.promotion = item
            binding.viewModel = viewModel
        }

        companion object {
            fun from(parent: ViewGroup): PromotionItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PromotionItemBinding.inflate(layoutInflater, parent, false)

                return PromotionItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionItemViewHolder {
        return PromotionItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PromotionItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
    }
}

class PromotionItemDiffCallback : DiffUtil.ItemCallback<Promotion>() {

    override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem == newItem
    }
}