package com.example.managertab

import android.icu.text.Transliterator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.store_detail.view.*
import kotlinx.android.synthetic.main.store_item.view.*

class StoreAdapter(private val storeList: MutableList<Store>) : RecyclerView.Adapter<StoreAdapter.StoreHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreHolder {
        val storeView = LayoutInflater.from(parent.context).inflate(R.layout.store_detail,
            parent,false)
        return StoreHolder(storeView)
    }

    override fun onBindViewHolder(holder: StoreHolder, position: Int) {
       val currentItem = storeList[position]

        when {
            currentItem.imageUrl?.trim().equals("") -> {
                Log.d("StoreItemAdapterImage","No Image Loaded")
                holder.imageView.setImageResource(R.drawable.ic_no_image)
            }
            currentItem.imageUrl == null -> {
                Log.d("StoreItemAdapterImage","Null Image Uri")

                holder.imageView.setImageResource(R.drawable.ic_add)
            }
            else -> {
                Picasso.with(holder.imageView.context)
                    .load(currentItem.imageUrl)
                    .resize(100, 100)
                    .centerCrop()
                    .into(holder.imageView)
            }
        }
        holder.storeName.text = currentItem.name
        holder.storeOwner.text = currentItem.ownerName
        val openTimeString = "Open Time:" + timeFormat(currentItem.closeTime)
        val closeTimeString = "Close TIme:" + timeFormat(currentItem.openTime)
        holder.openTime.text = openTimeString
        holder.closeTime.text = closeTimeString
        holder.hotLine.text = currentItem.hotline

    }
    private fun timeFormat(time : Int) : String{
        val hour = time / 60
        val minute = time % 60
        return "${String.format("%02d",hour)}:${String.format("%02d",minute)}"

    }
    override fun getItemCount(): Int {
        return storeList.size
    }
    class StoreHolder(storeView : View) : RecyclerView.ViewHolder(storeView){
        val imageView : ImageView = itemView.store_image
        val storeName : TextView = itemView.store_name_text
        val storeOwner : TextView = itemView.OwnerName
        val openTime : TextView = itemView.open_time_detail
        val closeTime : TextView = itemView.close_time_detail
        val hotLine : TextView = itemView.hotLine
    }
}




/*
class StoreAdapter(private val StoreList : MutableList<Store>) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {
        // Each of the View Holder is called when each of the view is created (for each Item)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
            val storeObject = LayoutInflater.from(parent.context).inflate(R.layout.store_detail,
                parent, false)
            return StoreViewHolder(storeObject)
        }

        // This function is call when any data is updated and when each item is recycled
        // This method is called over and over again
        override fun onBindViewHolder(holder: StoreAdapter.StoreViewHolder, position: Int) {
            val currentItem = StoreList[position]
            when {
                currentItem.imageUrl?.trim().equals("") -> {
                    Log.d("StoreItemAdapterImage","No Image Loaded")
                    holder.imageView.setImageResource(R.drawable.ic_no_image)
                }
                currentItem.imageUrl == null -> {
                    Log.d("StoreItemAdapterImage","Null Image Uri")

                    holder.imageView.setImageResource(R.drawable.ic_add)
                }
                else -> {
                    Picasso.with(holder.imageView.context)
                        .load(currentItem.imageUrl)
                        .resize(100, 100)
                        .centerCrop()
                        .into(holder.imageView)
                }
            }
            holder.nameTextView.text = currentItem.name
            holder.closeTimeTextView.text = currentItem.closeTime.toString()
            holder.openTimeTextView.text = currentItem.openTime.toString()
            holder.supportEmail.text = currentItem.supportEmail
            val text = holder.closeTimeTextView.text
            val imageText = currentItem.imageUrl.toString()
        }


        override fun getItemCount(): Int {
            return StoreList.size
        }
        // This class is used to avoid calling FindViewByID over and over again (Cache the view in the xml
        // so they can be updated faster and easier)
        inner class StoreViewHolder(StoreView: View) : RecyclerView.ViewHolder(StoreView) {
            val imageView: ImageView = StoreView.op
            val nameTextView: TextView = StoreView.store_name
            val openTimeTextView: TextView = StoreView.open_time
            val closeTimeTextView: TextView = StoreView.close_time
            val supportEmail: TextView = StoreView.support_email
        }
}
*/