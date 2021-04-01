package com.example.bk_foodcourt

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private var orders: List<Row>,
    private var context: Context
) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
//    fun ItemAdapter(orders: List<Row>, context: Context) {
//        this.orders = orders
//        this.context = context
//    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return ViewHolder(view, context, orders)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.orderID.setText(orders[position].getOrderID())
        holder.userID.setText(orders[position].getUserID())
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    inner class ViewHolder(
        itemView: View,
        context: Context,
        orders: List<Row>
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var orderID: TextView
        var userID: TextView
        var context: Context
        var orders: List<Row>
        override fun onClick(view: View) {
            //Toast.makeText(context, "Success!", Toast.LENGTH_SHORT)
//            val intent = Intent(context, ViewOrderActivity::class.java)
//            context.startActivity(intent)
        }

        init {
            orderID = itemView.findViewById(R.id.tvOrderID)
            userID = itemView.findViewById(R.id.tvUserID)
            itemView.setOnClickListener(this)
            this.context = context
            this.orders = orders
        }
    }

}