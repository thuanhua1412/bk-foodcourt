package com.example.bk_foodcourt.cook.order.orderDetail

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.databinding.CookOrderDetailBinding
import com.example.bk_foodcourt.notificationService.MessageQueue
import com.example.bk_foodcourt.order.Order
import com.example.bk_foodcourt.order.OrderItem
import com.example.bk_foodcourt.order.orderDetail.OrderItemAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.json.JSONObject

class CookOrderDetailFragment : Fragment() {
    private lateinit var binding: CookOrderDetailBinding
    private lateinit var adapter: OrderItemAdapter

    private lateinit var viewModelFactory: CookOrderDetailFactory
    private lateinit var viewModel: CookOrderDetailViewModel
    private lateinit var firestore: FirebaseFirestore

    private lateinit var responseView: View
    private  var cancelReason = ""
    private lateinit var order: Order

    companion object {
        private const val TAG = "OrderDetailFragment"
        private const val FCM_API = "https://fcm.googleapis.com/fcm/send"
        private const val serverKey =
            "key=AAAAJoqkuFM:APA91bHNoY2WeQieUpseXcRIXdsBN9oDpzXsyqvR4fj7ADU6M1zk8MNs7fW31wJsMDCfW3ntwihOMQgA7h-Whd-B3iaMrNwCRXO5ua_UQlGCB9rDJe1ti94bxIcquuejy4zoORaFWNYF"
        private const val contentType = "application/json"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.order_detail)

        firestore = FirebaseFirestore.getInstance()

        val inflater = requireActivity().layoutInflater
        responseView = inflater.inflate(R.layout.dialog_response, null)

        binding = CookOrderDetailBinding.inflate(inflater, container, false)

        val args = CookOrderDetailFragmentArgs.fromBundle(requireArguments())
        order = args.order
        viewModelFactory = CookOrderDetailFactory(args.order)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CookOrderDetailViewModel::class.java)

        binding.lifecycleOwner = this
        binding.order = order
        binding.viewModel = viewModel

        binding.cancelOrderButton.setOnClickListener { cancelOrder() }
        binding.processOrderButton.setOnClickListener { viewModel.processOrder() }
        binding.phoneButton.setOnClickListener { startCall() }

        setupRecyclerView()

        viewModel.message.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.sendCancelNotificationEvent.observe(viewLifecycleOwner, Observer {token ->
            token?.let {
                if (token.isNotEmpty()) {
                    val notification = JSONObject()
                    val body = JSONObject()

                    body.put("title", getString(R.string.order_cancelled))
                    body.put("message", getString(R.string.cancel_message, order.id, cancelReason))

                    notification.put("to", token)
                    notification.put("data", body)
                    sendNotification(notification)
                    viewModel.sendCancelNotificationEvent.value = null
                }
            }
        })
        viewModel.sendFinishNotificationEvent.observe(viewLifecycleOwner, Observer {token ->
            token?.let {
                if (token.isNotEmpty()) {
                    val notification = JSONObject()
                    val body = JSONObject()

                    body.put("title", getString(R.string.order_finished))
                    body.put("message", getString(R.string.finish_message, order.id))

                    notification.put("to", token)
                    notification.put("data", body)
                    sendNotification(notification)
                    viewModel.sendFinishNotificationEvent.value = null
                    navigateToOrderFragment()
                }
            }
        })
        viewModel.navigateToOrderFragmentEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                navigateToOrderFragment()
                viewModel.navigateToOrderFragmentEvent.value = null
            }
        })

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

    private fun cancelOrder() {
        val editText = responseView.findViewById<EditText>(R.id.cancel_reason)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.cancel_reason))
            .setView(responseView)
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                cancelReason = editText.text.toString()
                if (cancelReason.isEmpty()) {
                    Toast.makeText(context, R.string.cancel_requirement, Toast.LENGTH_LONG).show()
                } else {
                    viewModel.cancelOrder()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
            }
            .show()
    }

    private fun sendNotification(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "onResponse: $response")
            },
            Response.ErrorListener {
                Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show()
                Log.d(TAG, "onErrorResponse: Didn't work")
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        MessageQueue.getInstance(requireContext())?.addToRequestQueue(jsonObjectRequest)
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