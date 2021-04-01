package com.example.bk_foodcourt.menu.cart

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.databinding.CartFragmentBinding
import com.example.bk_foodcourt.home.Store
import com.example.bk_foodcourt.menu.Dish
import com.example.bk_foodcourt.notificationService.MessageQueue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import org.json.JSONObject

class CartFragment : Fragment() {
    private lateinit var binding: CartFragmentBinding
    private lateinit var adapter: CartItemAdapter

    private lateinit var viewModel: CartViewModel
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser
    private lateinit var store: Store

    companion object {
        private const val TAG = "CartFragment"
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
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.cart)

        val args = CartFragmentArgs.fromBundle(requireArguments())
        store = args.store

        firestore = FirebaseFirestore.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser!!

        binding = CartFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        viewModel.getStoreInfo(store)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.checkoutButton.setOnClickListener { checkOut() }

        adapter = CartItemAdapter(viewModel)
        viewModel.cartList.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                adapter.submitList(it)
                binding.orderDetail.adapter = adapter
            }
        })

        viewModel.codeList.observe(viewLifecycleOwner, Observer { codeList ->
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                codeList
            )
            binding.orderCode.setAdapter(arrayAdapter)
            binding.orderCode.setOnItemClickListener { parent, _, position, _ ->
                val index: Int = viewModel.codeList.value!!.indexOf(parent.getItemAtPosition(position))
                viewModel.applyPromotion(index)
            }
        })

        viewModel.showCartItemDetailEvent.observe(viewLifecycleOwner, Observer { dish ->
            dish?.let {
                showCartItemDetail(dish)
                viewModel.showCartItemDetailEvent.value = null
            }
        })

        viewModel.navigateToMenuFragmentEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, getString(R.string.empty_cart), Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
                viewModel.navigateToMenuFragmentEvent.value = null
            }
        })
        viewModel.goToHomeEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, getString(R.string.checkout_success), Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_cartFragment_to_storeFragment)
                viewModel.goToHomeEvent.value = null
            }
        })
        viewModel.sendNotificationEvent.observe(viewLifecycleOwner, Observer {token ->
            token?.let {
                if (token.isNotEmpty()) {
                    val notification = JSONObject()
                    val body = JSONObject()

                    body.put("title", getString(R.string.new_order))
                    body.put("message", getString(R.string.new_order_approve, store.name))

                    notification.put("to", token)
                    notification.put("data", body)
                    sendNotification(notification)
                    viewModel.sendNotificationEvent.value = null
                }
            }
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
                viewModel.errorMessage.value = null
            }
        })
        viewModel.clearTextEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.orderCode.setText("")
                viewModel.clearTextEvent.value = null
            }
        })

        setItemTouchHelper()

        return binding.root
    }

    private fun setItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeCartItem(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_24)
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorSecondary
                        )
                    )
                    .addSwipeLeftLabel(getString(R.string.delete))
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.orderDetail)
    }

    private fun checkOut() {
        binding.checkoutButton.isClickable = false
        val orderType = binding.orderType.selectedItem.toString()
        Log.d("CartFragment", orderType)
        viewModel.checkout(orderType, store.ownerID)
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

    private fun navigateToPaymentFragment() {
        findNavController().navigateUp()
    }

    private fun showCartItemDetail(dish: Dish) {
        val action = CartFragmentDirections.actionCartFragmentToDishFragment(dish)
        findNavController().navigate(action)
    }
}