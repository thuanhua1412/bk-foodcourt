package com.example.bk_foodcourt.account

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bk_foodcourt.databinding.PersonalInfoFragmentBinding
import com.example.bk_foodcourt.login.User
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bk_foodcourt.R

class PersonalInfoFragment : Fragment() {
    private lateinit var binding: PersonalInfoFragmentBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: User
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.personal_information)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        val args = PersonalInfoFragmentArgs.fromBundle(requireArguments())
        user = args.user

        binding = PersonalInfoFragmentBinding.inflate(inflater, container, false)
        binding.user = user

        binding.saveButton.setOnClickListener { saveInformation() }

        return binding.root
    }

    private fun saveInformation() {
        val name = binding.userName.text.toString().trim()
        val address = binding.userAddress.text.toString().trim()
        val phoneNumber = binding.userPhoneNumber.text.toString().trim()
        val email = binding.userEmail.text.toString().trim()

        if (name.isEmpty()) {
            binding.userName.error = resources.getString(R.string.empty_field)
        } else if (phoneNumber.length < 10) {
            binding.userPhoneNumber.error = resources.getString(R.string.invalid_phone_number)
        } else if (email.isEmpty()) {
            binding.userEmail.error = resources.getString(R.string.empty_field)
        } else {
            val updateMap = HashMap<String, Any>()
            val currentEmail = currentUser.email

            updateMap["name"] = name
            updateMap["phoneNumber"] = phoneNumber
            updateMap["address"] = address

            if (email != currentEmail) {
                val inflater = requireActivity().layoutInflater
                val view = inflater.inflate(R.layout.require_password_dialog, null)
                val editText = view.findViewById<EditText>(R.id.require_password)

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.require_password))
                    .setView(view)
                    .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                        val password = editText.text.toString()
                        val credential = EmailAuthProvider.getCredential(currentEmail!!, password)

                        currentUser.reauthenticate(credential)
                            .addOnSuccessListener {
                                updateUserEmail(email, updateMap)
                                dialog.dismiss()
                            }
                            .addOnFailureListener {
                                editText.error = getString(R.string.wrong_password)
                            }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
                    }
                    .show()
            } else {
                updateUser(updateMap)
            }
        }
    }

    private fun updateUserEmail(email: String, updateMap: HashMap<String, Any>) {
        val currentEmail = currentUser.email
        currentUser.updateEmail(email)
            .addOnSuccessListener {
                currentUser.sendEmailVerification()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.verify_email),
                            Toast.LENGTH_LONG
                        )
                            .show()
                        updateMap["email"] = email
                        updateUser(updateMap)
                    }
                firestore.collection("userTypes")
                    .whereEqualTo("email", currentEmail)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            for (document in querySnapshot) {
                                document.reference.update("email", email)
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthUserCollisionException -> Toast.makeText(
                        requireContext(),
                        getString(R.string.email_registered),
                        Toast.LENGTH_LONG
                    ).show()
                    else -> Toast.makeText(
                        requireContext(),
                        exception.toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
    }

    private fun updateUser(map: HashMap<String, Any>) {
        firestore.collection("users")
            .document(user.id)
            .update(map)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.update_success),
                    Toast.LENGTH_LONG
                )
                    .show()
                navigateToAccountFragment()
            }
    }

    private fun navigateToAccountFragment() {
        findNavController().navigate(R.id.accountFragment)
//        findNavController().navigateUp()
    }
}