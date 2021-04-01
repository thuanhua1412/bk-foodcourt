package com.example.bkmerchant.employee

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.EmployeeFragmentBinding
import com.example.bkmerchant.login.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class EmployeeFragment: Fragment() {
    private lateinit var binding: EmployeeFragmentBinding
    private lateinit var adapter: EmployeeAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.employees)

        firestore = FirebaseFirestore.getInstance()

        val args = EmployeeFragmentArgs.fromBundle(requireArguments())
        storeId = args.storeId

        binding = EmployeeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.addEmployeeFab.setOnClickListener { navigateToAddEmployeeFragment() }

        setupRecyclerView()

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removePermission(viewHolder.adapterPosition)
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
                    .addSwipeLeftLabel(getString(R.string.remove))
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

        }).attachToRecyclerView(binding.employeeList)

        return binding.root
    }

    private fun setupRecyclerView() {
        val query = firestore.collection("users")
            .whereEqualTo("storeID", storeId)

        val options = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        adapter = EmployeeAdapter(options)

        binding.employeeList.adapter = adapter
    }

    private fun navigateToAddEmployeeFragment() {
        val action = EmployeeFragmentDirections.actionEmployeeFragmentToAddEmployeeFragment(storeId)
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