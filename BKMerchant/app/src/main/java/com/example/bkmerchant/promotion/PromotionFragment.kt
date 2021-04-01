package com.example.bkmerchant.promotion

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.PromotionFragmentBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class PromotionFragment : Fragment() {
    private lateinit var binding: PromotionFragmentBinding
    private lateinit var adapter: PromotionAdapter
    private lateinit var viewModel: PromotionViewModel
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.promotions)

        val args = PromotionFragmentArgs.fromBundle(requireArguments())
        storeId = args.storeId

        binding = PromotionFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(PromotionViewModel::class.java)

        binding.addPromotionFab.setOnClickListener {
            navigateToPromotionDetailFragment(Promotion(storeId = storeId))
        }

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
                adapter.removePromotion(viewHolder.adapterPosition)
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

        }).attachToRecyclerView(binding.promotionRecycler)

        viewModel.showPromotionDetailEvent.observe(viewLifecycleOwner, Observer { promotion ->
            promotion?.let {
                navigateToPromotionDetailFragment(it)
                viewModel.showPromotionDetailEvent.value = null
            }
        })

        return binding.root
    }

    private fun setupRecyclerView() {
        val query: Query = FirebaseFirestore.getInstance()
            .collection("stores")
            .document(storeId)
            .collection("promotions")
            .orderBy("status", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Promotion>()
            .setQuery(query, Promotion::class.java)
            .build()

        adapter = PromotionAdapter(options, viewModel, storeId)

        binding.promotionRecycler.adapter = adapter
    }

    private fun navigateToPromotionDetailFragment(promotion: Promotion) {
        val action =
            PromotionFragmentDirections.actionPromotionFragmentToPromotionDetailFragment(promotion)
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