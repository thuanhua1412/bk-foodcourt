package com.example.bk_foodcourt.cook.menu

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.databinding.CookMenuFragmentBinding
import com.example.bk_foodcourt.login.LoginActivity
import com.example.bk_foodcourt.menu.Dish
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CookMenuFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: CookMenuFragmentBinding
    private lateinit var viewModel: CookMenuViewModel
    private var adapter: CookMenuAdapter? = null
    private var storeId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu)

        firebaseAuth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this).get(CookMenuViewModel::class.java)
        binding = CookMenuFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        viewModel.storeId.observe(viewLifecycleOwner, Observer {
            it?.let {id ->
                if (id.isEmpty()) {
                    Toast.makeText(context, getString(R.string.no_vendor_error), Toast.LENGTH_LONG)
                        .show()
                    viewModel.logout()
                } else {
                    storeId = id
                    setupRecyclerView()
                }
            }
        })
        viewModel.logoutEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                firebaseAuth.signOut()
                startLoginActivity()
            }
        })

        return binding.root
    }

    private fun setupRecyclerView() {
        val query: Query = FirebaseFirestore.getInstance()
            .collectionGroup("items")
            .whereEqualTo("storeId", storeId)
            .orderBy("availability", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Dish>()
            .setQuery(query, Dish::class.java)
            .build()

        adapter = CookMenuAdapter(options, viewModel)
        binding.menuItemRecycler.adapter = adapter
        adapter?.startListening()
    }

    private fun startLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}