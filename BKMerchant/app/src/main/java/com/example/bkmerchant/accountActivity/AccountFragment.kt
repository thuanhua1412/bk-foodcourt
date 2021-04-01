package com.example.bkmerchant.accountActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.example.bkmerchant.databinding.AccountFragmentBinding
import com.example.bkmerchant.login.LoginActivity
import com.example.bkmerchant.login.User
import com.example.bkmerchant.paymentActivity.PaymentActivity
import com.example.bkmerchant.storeActivity.StoreActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class AccountFragment: Fragment() {
    private lateinit var binding: AccountFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var viewModel: AccountViewModel

    private lateinit var storageReference: StorageReference
    private var userAvatarUri: Uri? = null
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    private lateinit var uploadDialog: AlertDialog
    private lateinit var uploadView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.account_setting)

        storageReference = FirebaseStorage.getInstance().getReference("user_images")

        val inflater = requireActivity().layoutInflater
        uploadView = inflater.inflate(R.layout.dialog_progress, null)
        uploadDialog = AlertDialog.Builder(requireContext())
            .setView(uploadView)
            .create()

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        if (firebaseAuth.currentUser == null) {
            logout()
        }

        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        binding = AccountFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.bottomNav.selectedItemId = R.id.nav_account
        binding.bottomNav.setOnNavigationItemSelectedListener {
            bottomNavigationItemSelected(it)
        }

        binding.profileImage.setOnClickListener {
            imageLoader()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
        binding.termPrivacy.setOnClickListener {
            navigateToTermFragment()
        }
        binding.aboutUs.setOnClickListener {
            navigateToAboutFragment()
        }
        binding.setting.setOnClickListener {
            navigateToSettingFragment()
        }
        binding.changePassword.setOnClickListener {
            navigateToPasswordFragment()
        }

        viewModel.navigateToPersonalInfoEvent.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                Log.d("AccountFragment", "show user info")
                navigateToPersonalInfoFragment(it)
                viewModel.navigateToPersonalInfoEvent.value = null
            }
        })

        return binding.root
    }

    private fun bottomNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
//                val intent = Intent(context, MainActivity::class.java)
//                startActivity(intent)
                activity?.finish()
            }
            R.id.nav_store -> {
                val intent = Intent(context, StoreActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            R.id.nav_payment -> {
                val intent = Intent(context, PaymentActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
        return true
    }

    private fun imageLoader() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1 && resultCode== Activity.RESULT_OK && data!=null && data.data!=null) {
            userAvatarUri = data.data
            uploadImage()
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
        userAvatarUri?.let {imageUri ->
            val fileReference = storageReference
                .child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))

            uploadDialog.show()

            uploadTask = fileReference.putFile(imageUri)
                .addOnSuccessListener {
                    Toast.makeText(context, getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                    fileReference.downloadUrl
                        .addOnSuccessListener {
                            Log.d("AccountFragment", it.toString())
                            viewModel.updateProfileImage(it.toString())
                            uploadDialog.dismiss()
                        }
                        .addOnFailureListener {
                            Log.d("AccountFragment", it.toString())
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                }
        }
    }

    private fun logout() {
        binding.logoutButton.isClickable = false
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { result ->
                Log.d("LoginFragment", "Current token: ${result.token}")
                firestore.collection("vendor_tokens")
                    .document(firebaseAuth.currentUser!!.uid)
                    .update("token", "")
                    .addOnSuccessListener {
                        Log.d("LoginFragment", "Update token successful")
                        firebaseAuth.signOut()
                        startLoginActivity()
                    }
                    .addOnFailureListener {
                        firebaseAuth.signOut()
                        startLoginActivity()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun startLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToTermFragment() {
        findNavController().navigate(R.id.termFragment)
    }

    private fun navigateToAboutFragment() {
        findNavController().navigate(R.id.aboutFragment)
    }

    private fun navigateToSettingFragment() {
        findNavController().navigate(R.id.settingsFragment)
    }

    private fun navigateToPasswordFragment() {
        findNavController().navigate(R.id.passwordFragment)
    }

    private fun navigateToPersonalInfoFragment(user: User) {
        val action = AccountFragmentDirections.actionAccountFragmentToPersonalInfoFragment(user)
        findNavController().navigate(action)
    }
}