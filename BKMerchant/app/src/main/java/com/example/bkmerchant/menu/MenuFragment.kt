package com.example.bkmerchant.menu

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.MenuFragmentBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class MenuFragment : Fragment() {
    companion object {
        const val TAG = "MenuFragment"
    }

    private lateinit var binding: MenuFragmentBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var adapter: NewMenuAdapter
    private lateinit var storeId: String

    private lateinit var categoryListView: ListView
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu)

        val arguments = MenuFragmentArgs.fromBundle(requireArguments())
        storeId = arguments.storeId

        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        viewModel.getCategoryList(storeId)
        binding = MenuFragmentBinding.inflate(inflater, container, false)
        binding.fabAddDish.setOnClickListener {
            onAddDish(Dish(storeId = storeId))
        }
        binding.fabAddCategory.setOnClickListener {
            onAddCategory(Category(storeId = storeId))
        }

        binding.lifecycleOwner = this

        setupRecyclerView()

        viewModel.openCategoryEvent.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                onAddCategory(it)
                viewModel.openCategoryEvent.value = null
            }
        })

        viewModel.openDishEvent.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                onAddDish(it)
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

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun onAddCategory(category: Category) {
        val action = MenuFragmentDirections.actionMenuFragmentToCategoryFragment(category)
        findNavController().navigate(action)
    }

    private fun onAddDish(dish: Dish) {
        viewModel.categories.value?.let {
            if (it.isEmpty()) {
                Toast.makeText(context, getString(R.string.no_category), Toast.LENGTH_LONG).show()
            } else {
                val action = MenuFragmentDirections.actionMenuFragmentToDishFragment(dish)
                findNavController().navigate(action)
            }
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