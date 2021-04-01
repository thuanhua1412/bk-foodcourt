package com.example.bk_foodcourt.home

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.account.AccountActivity
import com.example.bk_foodcourt.databinding.StoreFragmentBinding
import com.example.bk_foodcourt.menu.CartItem
import com.example.bk_foodcourt.order.CustomerOrderActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class StoreFragment: Fragment() {
    private lateinit var binding: StoreFragmentBinding
    private lateinit var adapter: StoreAdapter
    private lateinit var viewModel: StoreViewModel
    private lateinit var currentUser: FirebaseUser

    companion object {
        private const val TOPIC = "promotion"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        currentUser = FirebaseAuth.getInstance().currentUser!!

        viewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        binding = StoreFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.bottomNav.selectedItemId = R.id.nav_home
        binding.bottomNav.setOnNavigationItemSelectedListener {
            bottomNavigationItemSelected(it)
        }

        setupRecyclerView()

        viewModel.openStoreMenuEvent.observe(viewLifecycleOwner, Observer { store->
            if (store != null) {
                openStoreMenu(store)
                viewModel.openStoreMenuEvent.value = null
            }
        })

        subscribeTopic()

        return binding.root
    }

    private fun bottomNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_orderlist -> {
                val intent = Intent(context, CustomerOrderActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                val intent = Intent(context, AccountActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }

    private fun setupRecyclerView() {
        val query = FirebaseFirestore.getInstance()
            .collection("stores")
        val options = FirestoreRecyclerOptions.Builder<Store>()
            .setQuery(query, Store::class.java)
            .build()

        adapter = StoreAdapter(options, viewModel)
        binding.storeRecycler.adapter = adapter
    }

    private fun openStoreMenu(store: Store) {
//        val intent = Intent(requireContext(), OrderActivity::class.java)
//        startActivity(intent)
        val action = StoreFragmentDirections.actionStoreFragmentToMenuFragment(store)
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUser.uid)
            .collection("cart")
            .get()
            .addOnSuccessListener {querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    var item = CartItem()
                    for (document in querySnapshot) {
                        item = document.toObject(CartItem::class.java)
                        break
                    }
                    if (item.storeId != store.id) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(getString(R.string.warning))
                            .setMessage(R.string.proceed_new_cart)
                            .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
                            }
                            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                                for (document in querySnapshot) {
                                    document.reference.delete()
                                }
                                findNavController().navigate(action)
                            }

                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        findNavController().navigate(action)
                    }
                } else {
                    findNavController().navigate(action)
                }
            }
    }

    private fun subscribeTopic() {
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener {
            }
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