package com.example.bk_foodcourt.menu

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.databinding.MenuFragmentBinding
import com.example.bk_foodcourt.home.Store
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MenuFragment : Fragment() {
    private lateinit var binding: MenuFragmentBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var viewModelFactory: MenuViewModelFactory
    private lateinit var adapter: NewMenuAdapter
    private lateinit var promotionAdapter: PromotionAdapter
    private lateinit var storeId: String
    private lateinit var store: Store

    private lateinit var categoryListView: ListView
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val arguments = MenuFragmentArgs.fromBundle(requireArguments())
        store = arguments.store
        storeId = store.id
        (activity as AppCompatActivity).supportActionBar?.title = store.name

        viewModelFactory = MenuViewModelFactory(storeId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MenuViewModel::class.java)
        viewModel.checkEmptyCart()
        binding = MenuFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.store = store

        setupRecyclerView()
        promotionAdapter = PromotionAdapter(viewModel)
        viewModel.promotionList.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                promotionAdapter.submitList(list)
                binding.discountRecycler.adapter = promotionAdapter
            }
        })

        viewModel.loadDiscountDone.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.loadDiscountDone.value = null
            }
        })

        viewModel.openDishEvent.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                navigateToDishDetail(it)
                viewModel.openDishEvent.value = null
            }
        })
        context?.let {
            categoryListView = ListView(it)

            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.choose_one_category))
                .setCancelable(true)
            dialog = builder.create()

            dialog.window?.setGravity(Gravity.TOP or Gravity.END)

            categoryListView.setOnItemClickListener { _, _, position, _ ->
                binding.menuItemRecycler.smoothScrollToPosition(position)
                dialog.dismiss()
            }
        }

        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            val arrayAdapter =
                context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, categories) }
            categoryListView.adapter = arrayAdapter
        })

        viewModel.isEmptyCart.observe(viewLifecycleOwner, Observer {
            it?.let {isEmpty ->
                if (!isEmpty) {
                    binding.cartFab.visibility = View.VISIBLE
                } else {
                    binding.cartFab.visibility = View.GONE
                }
            }
        })

        binding.cartFab.setOnClickListener {
            showCart()
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun showCart() {
        val action = MenuFragmentDirections.actionMenuFragmentToCartFragment(store)
        findNavController().navigate(action)
    }

    private fun navigateToDishDetail(dish: Dish) {
        if (dish.availability) {
            val action = MenuFragmentDirections.actionMenuFragmentToDishFragment(dish)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        val layoutManger = SmoothLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val query: Query = FirebaseFirestore.getInstance()
            .collection("stores")
            .document(storeId)
            .collection("categories")
            .orderBy("priority", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Category>()
            .setQuery(query, Category::class.java)
            .build()

        adapter = NewMenuAdapter(options, viewModel, viewLifecycleOwner, storeId)
        binding.menuItemRecycler.adapter = adapter
        binding.menuItemRecycler.layoutManager = layoutManger
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter -> {
                openDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDialog() {
        dialog.setView(categoryListView)
        dialog.show()
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