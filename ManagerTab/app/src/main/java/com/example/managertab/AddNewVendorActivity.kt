package com.example.managertab

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.managertab.databinding.ActivityAddNewVendorBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.lang.Integer.parseInt
import java.util.*

class AddNewVendorActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAddNewVendorBinding
    private val pickImageRequest : Int = 1
    private lateinit var vendorImageView : ImageView
    private lateinit var imageUri : Uri
    private var firebaseStorage: StorageReference? = null
    private var firebaseDatabase : DatabaseReference? = null
    private lateinit var firebaseFireStore : FirebaseFirestore
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    // Data object of class Store to store the whole vendor Information
    private lateinit var vendorInfo : Store
    private lateinit var imageURL : String
    // Obtain ViewModel from ViewModelProviders
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AddVendorViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_vendor)
        binding.lifecycleOwner = this  // use Fragment.viewLifecycleOwner for fragments
        binding.managerViewModel = viewModel
        vendorImageView = binding.vendorImageView
        // Remember to edit the location in this line when merge with the whole team's APP
        firebaseStorage = FirebaseStorage.getInstance().getReference("manager_images")
        // getDatabase reference to upload the store information onto the firebase database
        firebaseDatabase =  FirebaseDatabase.getInstance().reference
        // Get FirebaseFirestore reference to upload the store information to firestore
        firebaseFireStore = FirebaseFirestore.getInstance()
        // Upload (Image) Button in the Add Vendor Tab
        binding.chooseImageButton.setOnClickListener{
            openFileChooser()
        }
        binding.uploadButton.setOnClickListener{
            Toast.makeText(this, "Your Image is being uploaded", Toast.LENGTH_SHORT).show()
            uploadFile()
        }
        // Set store attributes to the infos the manager type in the apps;
        vendorInfo = Store()
        binding.openTimeEditText.setOnClickListener{
            openTimePicker()
        }
        binding.closeTimeEditText.setOnClickListener{
            closeTimePicker()
        }

        // Upload (whole Store information) Button in the Add Vendor Tab
        binding.doneButton.setOnClickListener{
            if (fieldsValidated()){
                vendorInfo.description = binding.vendorDescriptionEditText.text.toString()
                vendorInfo.hotline = binding.hotLineEditText.text.toString()
                if (imageURL.trim() != "") {
                    vendorInfo.imageUrl = imageURL
                }
                else{
                    vendorInfo.imageUrl = ""
                }
                vendorInfo.name = binding.vendorNameEditText.text.toString()
                vendorInfo.ownerName = binding.vendorOwnerNameEditText.text.toString()
                vendorInfo.supportEmail = binding.supportMailEditText.text.toString()
                vendorInfo.website = binding.websiteEditText.text.toString()
                val storeID : String? = firebaseDatabase!!.child("stores").push().key
                if (storeID != null) {
                    Toast.makeText(this, "UPLOADING STORE", Toast.LENGTH_SHORT).show()
                    uploadStore()
                    finish()
                }
                else {
                    Toast.makeText(this, "Error getting new ID on the Database", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this,"Some Field is missing, Make sure that every fields is correctly filled",Toast.LENGTH_SHORT).show()
            }
        }

    }
    //-------------------------------------------------------------------------------------------------------
    /* These codes are for uploading the image to the Database and load the Image into the Image
    View in the Add Vendor Tab
     */
    private fun openFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, pickImageRequest)
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK && data != null && data.data != null
        ) {
            imageUri = data.data!!
            binding.vendorImageView.setImageURI(imageUri)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadFile() {
        imageUri.let {
            val fileReference: StorageReference = firebaseStorage!!.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(imageUri)
            )
            uploadTask = fileReference.putFile(imageUri)
                .addOnSuccessListener {
                    Toast.makeText(this, "Grats. Your Image is now uploaded " +
                            "to the Database", Toast.LENGTH_SHORT).show()
                    fileReference.downloadUrl
                        .addOnCompleteListener {
                            it.addOnSuccessListener {
                                imageURL = it.toString()
                                Log.d("Upload Image", "imageURL:$imageURL")
                            }
                                .addOnFailureListener{
                                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener{
                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
//                .addOnProgressListener { taskSnapshot ->
//                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
//                    binding.progressUploadImage.progress = progress.toInt()
//                }
        }

    }
    //-----------------------------------------------------------------------------------------------
    /*The following codes are for uploading the whole vendor image onto the database child collection
    named stores (with fields specified in the Store class)
     */
    private fun uploadStore(){
        // Upload to Firebase DATABASE
        // Upload TO FIREBASE FIRESTORE:
            val store = Store("",vendorInfo.name,vendorInfo.imageUrl,vendorInfo.hotline
            ,vendorInfo.website,vendorInfo.supportEmail,vendorInfo.description,vendorInfo.ownerName
            ,true,vendorInfo.openTime,vendorInfo.closeTime)
            firebaseFireStore.collection("stores")
                .add(store)
                .addOnSuccessListener {
                    Log.d("TAG", "DocumentSnapshot written with ID: ${it.id}")
                }
                .addOnCanceledListener {
                    Log.d("TAG", "DocumentSnapshot Canceled")
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Failed to write DOcucment", Toast.LENGTH_SHORT).show()
                }
    }
    /* This function make sure that all the editexts are filled before the store is uploaded
     */
    private fun fieldsValidated() : Boolean{
        when {
            binding.closeTimeEditText.text.isEmpty() -> {
                binding.closeTimeEditText.error = "Vendor's Close time is required"
                return false
            }
            binding.openTimeEditText.text.isEmpty()  -> {
                binding.openTimeEditText.error ="Vendor's Open time is required"
                return false
            }
            binding.hotLineEditText.text.isEmpty() -> {
                binding.hotLineEditText.error = "This field is required"
                return false
            }
            binding.vendorNameEditText.text.isEmpty() -> {
                binding.vendorNameEditText.error = "Store name is required"
                return false
            }
            binding.vendorOwnerNameEditText.text.isEmpty() -> {
                binding.vendorOwnerNameEditText.error = "Store Owner need to be input"
                return false
            }
            else -> return true
        }
    }
    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            val timeText = "${String.format("%02d",hourOfDay)}: ${String.format("%02d",minute)}"
            binding.openTimeEditText.setText(timeText)
            vendorInfo.openTime = hourOfDay * 60 + minute
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    private fun closeTimePicker() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            val timeText = "${String.format("%02d",hourOfDay)}: ${String.format("%02d",minute)}"
            binding.closeTimeEditText.setText(timeText)
            vendorInfo.closeTime = hourOfDay * 60 + minute
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }
}

//-------------------------------------------------------CODE GRAVE YARD---------------------------------------------
/*
fun uploadImage(){
if (imageUri != null) {
            val fileReference: StorageReference = firebaseStorage!!.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(imageUri)
            )
            uploadTask = fileReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    val handler = Handler()
                    handler.postDelayed(Runnable { binding.progressUploadImage.progress = 0 }, 500)
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG)
                        .show()
                    val upload = Upload(
                        binding.editTextFileName.text.toString().trim(),
                        firebaseStorage!!.downloadUrl.toString()
                    )
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    binding.progressUploadImage.progress = progress.toInt()
                }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
}*/