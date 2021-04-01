package com.example.managertab
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration.get
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.managertab.R
import com.example.managertab.R.drawable
import com.example.managertab.Store
import com.example.managertab.StoreItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.store_item.view.*
import java.lang.reflect.Array.get
import kotlin.math.log

class StoreItemAdapter(
    private val StoreItemList : MutableList<StoreItem>,
    private val listener: StoreItemClickListener
) : Adapter<StoreItemAdapter.StoreItemViewHolder>(){
    // Each of the View Holder is called when each of the view is created (for each Item)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
        val storeItemObject = LayoutInflater.from(parent.context).inflate(R.layout.store_item,
        parent, false)

        return StoreItemViewHolder(storeItemObject)
    }

    // This function is call when any data is updated and when each item is recycled
    // This method is called over and over again
    override fun onBindViewHolder(holder: StoreItemViewHolder, position: Int) {
        val currentItem = StoreItemList[position]
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
        holder.closeTimeTextView.text = currentItem.closeTime?.let { timeFormat(it) }
        holder.openTimeTextView.text = currentItem.openTime?.let { timeFormat(it) }
        holder.supportEmail.text = currentItem.supportEmail
        val text = holder.closeTimeTextView.text
        val imageText = currentItem.imageUrl.toString()
    }
    private fun timeFormat(time : Int) : String{
        val hour = time / 60
        val minute = time % 60
        return "${String.format("%02d",hour)}:${String.format("%02d",minute)}"

    }

    override fun getItemCount(): Int {
        return StoreItemList.size
    }
    // This class is used to avoid calling FindViewByID over and over again (Cache the view in the xml
    // so they can be updated faster and easier)
    inner class StoreItemViewHolder(StoreItemView: View) : RecyclerView.ViewHolder(StoreItemView)
    , View.OnClickListener{
        val imageView : ImageView = StoreItemView.image_view
        val nameTextView : TextView = StoreItemView.store_name
        val openTimeTextView : TextView = StoreItemView.open_time
        val closeTimeTextView  : TextView = StoreItemView.close_time
        val supportEmail : TextView = StoreItemView.support_email
        init {
            StoreItemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position  = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onStoreClicked(position)
            }
        }
    }
    interface StoreItemClickListener{
        fun onStoreClicked(position: Int)
    }

}
