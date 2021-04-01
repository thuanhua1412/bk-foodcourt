package com.example.bkmerchant.order.orderList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bkmerchant.R
import com.example.bkmerchant.order.Order
import com.example.bkmerchant.order.OrderFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OngoingOrderFragment(val storeId: String) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: OrderViewModel
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.order_list_fragment, container, false)
        recyclerView = view.findViewById(R.id.order_list)

        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        setupRecyclerView()

        viewModel.showOrderDetailEvent.observe(viewLifecycleOwner, Observer { order ->
            order?.let {
                navigateToOrderDetailFragment(it)
                viewModel.showOrderDetailEvent.value = null
            }
        })

        return view
    }

    private fun setupRecyclerView() {
        val query: Query = FirebaseFirestore.getInstance()
            .collection("order")
            .whereEqualTo("storeID", storeId)
            .whereLessThan("status", 3)
            .orderBy("status")
            .orderBy("time", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        adapter = OrderAdapter(options, viewModel)

        recyclerView.adapter = adapter
    }

    private fun navigateToOrderDetailFragment(order: Order) {
        val action = OrderFragmentDirections.actionOrderFragmentToOrderDetailFragment(order)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}