package com.example.bkmerchant.promotion

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bkmerchant.databinding.PromotionItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PromotionAdapter(
    options: FirestoreRecyclerOptions<Promotion>,
    val viewModel: PromotionViewModel
    , private val storeId: String
) :
    FirestoreRecyclerAdapter<Promotion, PromotionAdapter.PromotionViewHolder>(options) {

    class PromotionViewHolder private constructor(val binding: PromotionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Promotion, viewModel: PromotionViewModel) {
            binding.promotion = item
            binding.viewModel = viewModel
        }

        companion object {
            fun from(parent: ViewGroup): PromotionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PromotionItemBinding.inflate(layoutInflater, parent, false)

                return PromotionViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        Log.d("PromotionAdapter", "Create promotion view holder")
        return PromotionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int, item: Promotion) {
        item.id = snapshots.getSnapshot(position).id
        item.storeId = storeId
        holder.bind(item, viewModel)
    }

    fun removePromotion(position: Int) {
        snapshots.getSnapshot(position).reference.delete()
    }
}