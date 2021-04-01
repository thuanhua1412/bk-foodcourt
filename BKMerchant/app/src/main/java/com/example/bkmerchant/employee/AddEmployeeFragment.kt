package com.example.bkmerchant.employee

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.EmployeeAddFragmentBinding
import com.example.bkmerchant.login.AccountType
import com.example.bkmerchant.login.UserType
import com.google.firebase.firestore.FirebaseFirestore

class AddEmployeeFragment : Fragment() {
    private lateinit var binding: EmployeeAddFragmentBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.employee_add)

        firestore = FirebaseFirestore.getInstance()

        val args = AddEmployeeFragmentArgs.fromBundle(requireArguments())
        storeId = args.storeId

        binding = EmployeeAddFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.doneFab.setOnClickListener { addCookPermission() }

        return binding.root
    }

    private fun addCookPermission() {
        Log.d("AddFragment", "check")
        val email = binding.employeeEmail.text.toString()

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("AddFragment", "Valid")
            firestore.collection("userTypes")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        val userType = UserType(email, AccountType.COOK)
                        firestore.collection("users")
                            .whereEqualTo("email", email)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { query ->
                                if (query.isEmpty) {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.email_not_exist),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    for (document in query) {
                                        document.reference.update("storeID", storeId)
                                    }
                                    firestore.collection("userTypes")
                                        .add(userType)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.add_permission_success),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navigateToEmployeeFragmnet()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                requireContext(),
                                                it.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.permision_invalid),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                }
        } else {
            Log.d("AddFragment", "Invalid")
            binding.employeeEmail.error = getString(R.string.invalid_email)
            binding.employeeEmail.requestFocus()
        }
    }

    private fun navigateToEmployeeFragmnet() {
        findNavController().navigateUp()
    }
}