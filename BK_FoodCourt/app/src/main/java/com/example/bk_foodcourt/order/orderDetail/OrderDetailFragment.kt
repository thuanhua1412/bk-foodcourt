package com.example.bk_foodcourt.order.orderDetail

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.databinding.OrderDetailBinding
import com.example.bk_foodcourt.order.Order
import com.example.bk_foodcourt.order.OrderItem
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrderDetailFragment : Fragment() {
    private lateinit var binding: OrderDetailBinding
    private lateinit var adapter: OrderItemAdapter

    private lateinit var viewModelFactory: OrderDetailFactory
    private lateinit var viewModel: OrderDetailViewModel
    private lateinit var firestore: FirebaseFirestore

    private lateinit var order: Order

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.order_detail)

        firestore = FirebaseFirestore.getInstance()

        binding = OrderDetailBinding.inflate(inflater, container, false)

        val args = OrderDetailFragmentArgs.fromBundle(requireArguments())
        order = args.order
        viewModelFactory = OrderDetailFactory(order)
        viewModel = ViewModelProvider(this, viewModelFactory).get(OrderDetailViewModel::class.java)

        binding.order = order
        binding.cancelOrderButton.setOnClickListener { cancelOrder() }
        binding.finishOrderButton.setOnClickListener { finishOrder() }
        binding.phoneButton.setOnClickListener { startCall() }

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        Log.d("OrderDetailFragment", order.id)
        val query: Query = firestore.collection("order")
            .document(order.id)
            .collection("orderItems")

        val options = FirestoreRecyclerOptions.Builder<OrderItem>()
            .setQuery(query, OrderItem::class.java)
            .build()

        adapter = OrderItemAdapter(options)

        binding.orderDetail.adapter = adapter
    }

    private fun finishOrder() {
        firestore.collection("order")
            .document(order.id)
            .update("status", 3)
            .addOnSuccessListener {
                navigateToOrderFragment()
            }
    }

    private fun cancelOrder() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.confirm_cancel))
            .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
            }
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                firestore.collection("order")
                    .document(order.id)
                    .update("status", 4)
                    .addOnSuccessListener {
                        navigateToOrderFragment()
                    }
            }
            .show()
    }

    private fun navigateToOrderFragment() {
        findNavController().navigateUp()
    }

    private fun startCall() {
        val phone = order.userPhoneNumber
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
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