package com.example.bkmerchant.storeActivity.storeDetail

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.StoreDetailFragmentBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.util.*

class StoreDetailFragment : Fragment() {
    private lateinit var binding: StoreDetailFragmentBinding
    private lateinit var viewModel: StoreDetailViewModel
    private lateinit var viewModelFactory: StoreDetailFactory

    private lateinit var storageReference: StorageReference
    private var storeImageUri: Uri? = null
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.store_detail)

        storageReference = FirebaseStorage.getInstance().getReference("store_images")

        val arguments = StoreDetailFragmentArgs.fromBundle(requireArguments())
        viewModelFactory = StoreDetailFactory(arguments.store)
        viewModel = ViewModelProvider(this, viewModelFactory).get(StoreDetailViewModel::class.java)

        binding = StoreDetailFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.storeImage.setOnClickListener {
            imageLoader()
        }
        binding.doneFab.setOnClickListener {
            checkValidInformation()
        }
        binding.openTime.setOnClickListener {
            openTimePicker()
        }
        binding.closeTime.setOnClickListener {
            closeTimePicker()
        }

        viewModel.navigateToMenuFragment.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                navigateToStoreFragment()
            }
        })

        return binding.root
    }

    private fun openTimePicker() {
        val currentHour = viewModel.openTime.value!! / 60
        val currentMinute = viewModel.openTime.value!! % 60

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            viewModel.openTime.value = hourOfDay * 60 + minute
        }
        TimePickerDialog(context, timeSetListener, currentHour, currentMinute, false).show()
    }

    private fun closeTimePicker() {
        val currentHour = viewModel.closeTime.value!! / 60
        val currentMinute = viewModel.closeTime.value!! % 60

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            viewModel.closeTime.value = hourOfDay * 60 + minute
        }
        TimePickerDialog(context, timeSetListener, currentHour, currentMinute, false).show()
    }

    private fun imageLoader() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            storeImageUri = data.data
            binding.storeImage.setImageURI(data.data)
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
        storeImageUri?.let { imageUri ->
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
                            Log.d("StoreDetailFragment", it.toString())
                            viewModel.updateStore(it.toString())

                            binding.loadingProgress.visibility = View.GONE
                            binding.doneFab.isEnabled = true
                        }
                        .addOnFailureListener {
                            Log.d("StoreDetailFragment", it.toString())
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
        if (storeImageUri == null) {
            viewModel.updateStore("")
        }
    }

    private fun checkValidInformation() {
        var check = true
        val name = viewModel.name.value ?: ""
        val hotline = viewModel.hotline.value ?: ""
        val openTime = viewModel.openTime.value ?: 0
        val closeTime = viewModel.closeTime.value ?: 0
        if (name.trim().isEmpty()) {
            check = false
            binding.storeNameText.error = getString(R.string.empty_field)
        } else if (hotline.trim().length < 10) {
            check = false
            binding.storeHotline.error = getString(R.string.invalid_phone_number)
        }
        if (openTime >= closeTime) {
            check = false
            Toast.makeText(context, getString(R.string.time_lesser), Toast.LENGTH_SHORT)
                .show()
        }
        if (check) {
            uploadImage()
        }
    }

    private fun navigateToStoreFragment() {
        findNavController().navigateUp()
    }

}