package com.example.bk_foodcourt.cook.order.orderList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.cook.order.CookOrderFragmentDirections
import com.example.bk_foodcourt.order.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CookNewOrderFragment(private val storeId: String) : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: CookOrderViewModel
    private lateinit var adapter: CookOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.order_list_fragment, container, false)
        recyclerView = view.findViewById(R.id.order_list)

        viewModel = ViewModelProvider(this).get(CookOrderViewModel::class.java)

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
            .whereEqualTo("status", 0)
            .whereEqualTo("storeID", storeId)
            .orderBy("time", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        adapter = CookOrderAdapter(options, viewModel)

        recyclerView.adapter = adapter
    }

    private fun navigateToOrderDetailFragment(order: Order) {
        val action = CookOrderFragmentDirections.actionCookOrderFragmentToCookOrderDetailFragment(order)
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