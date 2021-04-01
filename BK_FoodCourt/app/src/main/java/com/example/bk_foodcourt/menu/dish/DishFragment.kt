package com.example.bk_foodcourt.menu.dish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.databinding.DishDetailFragmentBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DishFragment : Fragment() {
    private lateinit var binding: DishDetailFragmentBinding
    private lateinit var viewModelFactory: DishViewModelFactory
    private lateinit var viewModel: DishViewModel

    private lateinit var storageReference: StorageReference
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.dish_detail)

        storageReference = FirebaseStorage.getInstance().getReference("dish_image")

        val args = DishFragmentArgs.fromBundle(requireArguments())
        viewModelFactory = DishViewModelFactory(args.dish)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DishViewModel::class.java)

        binding = DishDetailFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.dish = args.dish

        binding.addButton.setOnClickListener {
            viewModel.increment()
        }

        binding.subButton.setOnClickListener {
            viewModel.decrement()
        }

        binding.cartButton.setOnClickListener {
            viewModel.saveCart()
        }

        viewModel.navigateToMenuFragment.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                navigateToMenuFragment()
                viewModel.navigateToMenuFragment.value = null
            }
        })

        return binding.root
    }

    private fun navigateToMenuFragment() {
        findNavController().navigateUp()
    }
}