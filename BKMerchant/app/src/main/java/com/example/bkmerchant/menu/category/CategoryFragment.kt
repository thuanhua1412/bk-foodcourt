package com.example.bkmerchant.menu.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.CategoryFragmentBinding

class CategoryFragment : Fragment() {

    private lateinit var binding: CategoryFragmentBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var viewModelFactory: CategoryViewModelFactory
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.category)

        val args = CategoryFragmentArgs.fromBundle(requireArguments())
        storeId = args.category.storeId
        viewModelFactory = CategoryViewModelFactory(args.category)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

        binding = CategoryFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigateToMenuFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                navigateToMenuFragment()
            }
        })

        viewModel.nameFieldError.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.categoryNameText.error = getString(it)
            }
        })

        setupDropdownList()

        return binding.root
    }

    private fun setupDropdownList() {
        val priorityAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.priority,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.categoryPriority.setText(priorityAdapter.getItem(viewModel.priority), false)
        binding.categoryPriority.setAdapter(priorityAdapter)
        binding.categoryPriority.setOnItemClickListener { _, _, position, _ ->
            viewModel.priority = position
        }
    }

    private fun navigateToMenuFragment() {
        findNavController().navigateUp()
    }
}