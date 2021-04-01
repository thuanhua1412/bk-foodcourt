package com.example.my_app.cook.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.my_app.R;
import com.example.my_app.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<OrderItem> orderList = new ArrayList<>();
    //private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore mFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view =  inflater.inflate(R.layout.fragment_order, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("order")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String order = document.get("id").toString();
                                String customer = document.get("userName").toString();
                                String vendor = document.get("storeName").toString();
                                String imageUrl = document.get("imageUrl").toString();
                                orderList.add(new OrderItem(imageUrl, order, customer, vendor));
                            }
                            mAdapter = new OrderAdapter(orderList);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        else {
                            Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return view;
    }
}