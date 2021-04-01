package com.example.bkmerchant.storeActivity.store

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bkmerchant.R
import com.example.bkmerchant.accountActivity.AccountActivity
import com.example.bkmerchant.databinding.StoreFragmentBinding
import com.example.bkmerchant.paymentActivity.PaymentActivity
import com.example.bkmerchant.storeActivity.Store
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StoreFragment: Fragment() {
    private lateinit var binding: StoreFragmentBinding
    private lateinit var adapter: StoreAdapter
    private lateinit var currentUser: FirebaseUser
    private lateinit var viewModel: StoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.store)

        viewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        currentUser = FirebaseAuth.getInstance().currentUser!!

        binding = StoreFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.bottomNav.selectedItemId = R.id.nav_store
        binding.bottomNav.setOnNavigationItemSelectedListener {
            bottomNavigationItemSelected(it)
        }

        viewModel.openStoreDetailEvent.observe(viewLifecycleOwner, Observer { store->
            if (store != null) {
                openStoreDetail(store)
                viewModel.openStoreDetailEvent.value = null
            }
        })

        setupRecyclerView()

        return binding.root
    }

    private fun bottomNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
//                val intent = Intent(context, MainActivity::class.java)
//                startActivity(intent)
                activity?.finish()
            }
            R.id.nav_account -> {
                val intent = Intent(context, AccountActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            R.id.nav_payment -> {
                val intent = Intent(context, PaymentActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
        return true
    }

    private fun setupRecyclerView() {
        val query = FirebaseFirestore.getInstance().collection("stores")
            .whereEqualTo("ownerID", currentUser.uid)
            .orderBy("isFocus", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Store>()
            .setQuery(query, Store::class.java)
            .build()

        adapter = StoreAdapter(options, viewModel)
        binding.storeRecycler.adapter = adapter
    }

    private fun openStoreDetail(    store: Store) {
        val action = StoreFragmentDirections.actionStoreFragmentToStoreDetailFragment(store)
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