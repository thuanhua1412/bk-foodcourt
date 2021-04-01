package com.example.bkmerchant.menu.dish

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.DishFragmentBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class DishFragment : Fragment() {
    private lateinit var binding: DishFragmentBinding
    private lateinit var viewModelFactory: DishViewModelFactory
    private lateinit var viewModel: DishViewModel

    private lateinit var storageReference: StorageReference
    private var dishImageUri: Uri? = null
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.dish)

        storageReference = FirebaseStorage.getInstance().getReference("dish_image")

        val args = DishFragmentArgs.fromBundle(requireArguments())
        storeId = args.dish.storeId
        viewModelFactory = DishViewModelFactory(args.dish)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DishViewModel::class.java)

        binding = DishFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.nameFieldError.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.dishNameText.error = getString(it)
            }
        })

        binding.dishImage.setOnClickListener {
            imageLoader()
        }
        binding.doneFab.setOnClickListener {
            checkEmptyField()
        }

        viewModel.navigateToMenuFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                navigateToMenuFragment()
            }
        })

        viewModel.catList.observe(viewLifecycleOwner, Observer { categories ->
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
            )
            binding.dishCategory.setAdapter(arrayAdapter)
            binding.dishCategory.setText(arrayAdapter.getItem(viewModel.currentIndex), false)
            binding.dishCategory.setOnItemClickListener { _, _, position, _ ->
                viewModel.categoryIndex = position
            }
        })

        return binding.root
    }

    private fun imageLoader() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            dishImageUri = data.data
            binding.dishImage.setImageURI(dishImageUri)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = activity?.contentResolver
        val mimeType = MimeTypeMap.getSingleton()
        if (contentResolver != null) {
            return mimeType.getExtensionFromMimeType(contentResolver.getType(uri))
        }
        return null
    }

    private fun uploadImage() {
        dishImageUri?.let { imageUri ->
            val fileReference = storageReference
                .child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))
            binding.loadingProgress.visibility = View.VISIBLE
            binding.doneFab.isEnabled = false

            uploadTask = fileReference.putFile(imageUri)
                .addOnSuccessListener {
                    Toast.makeText(context, getString(R.string.update_success), Toast.LENGTH_SHORT)
                        .show()
                    fileReference.downloadUrl
                        .addOnSuccessListener {
                            Log.d("DishFragment", it.toString())
                            viewModel.saveDish(it.toString())

                            binding.loadingProgress.visibility = View.GONE
                            binding.doneFab.isEnabled = true
                        }
                        .addOnFailureListener {
                            Log.d("DishFragment", it.toString())
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { task ->
                    val progress = 100.0 * task.bytesTransferred / task.totalByteCount
                    binding.loadingProgress.progress = progress.toInt()
                }
        }
        if (dishImageUri == null) {
            viewModel.saveDish("")
        }
    }

    private fun checkEmptyField() {
        var check = true
        val name = viewModel.name.value ?: ""
        val price = viewModel.price.value ?: ""
        if (name.trim().isEmpty()) {
            check = false
            binding.dishNameText.error = getString(R.string.empty_field)
        }
        if (price.isEmpty()) {
            check = false
            binding.dishPriceText.error = getString(R.string.empty_field)
        }
        if (check) {
            uploadImage()
        }
    }

    private fun navigateToMenuFragment() {
        findNavController().navigateUp()
    }
}