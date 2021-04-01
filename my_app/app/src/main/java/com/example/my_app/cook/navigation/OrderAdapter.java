package com.example.my_app.cook.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.example.my_app.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private ArrayList<OrderItem> mOrderList;
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView1);
            mTextView3 = itemView.findViewById(R.id.textView2);
        }
    }

    public OrderAdapter(ArrayList<OrderItem> orderList) {
        mOrderList = orderList;
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        OrderViewHolder ovh = new OrderViewHolder(view);
        return ovh;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem currentItem = mOrderList.get(position);

        if (currentItem.getImageUrl() == null) {
            holder.mImageView.setImageResource(R.drawable.common_google_signin_btn_icon_disabled);
        }
        else if (currentItem.getImageUrl().trim().equals("")) {
            holder.mImageView.setImageResource(R.drawable.common_google_signin_btn_icon_disabled);
        }
        else {
            Picasso.with(holder.mImageView.getContext())
                    .load(currentItem.getImageUrl())
                    .resize(80, 80)
                    .centerCrop()
                    .into(holder.mImageView);
        }
        holder.mTextView1.setText("Order: " + currentItem.getOrder());
        holder.mTextView2.setText("Customer: " + currentItem.getCustomer());
        holder.mTextView3.setText("Vendor: " + currentItem.getVendor());
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
