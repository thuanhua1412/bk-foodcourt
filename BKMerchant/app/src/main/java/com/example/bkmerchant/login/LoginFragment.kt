package com.example.bkmerchant.login

import android.content.Intent
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
import com.example.bkmerchant.MainActivity
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.LoginFragmentBinding
import com.example.bkmerchant.notificationService.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId

class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.login)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (firebaseAuth.currentUser != null) {
            startMainActivity()
        }

        binding = LoginFragmentBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener { login() }
        binding.createAccountButton.setOnClickListener { navigateToRegisterFragment() }
        binding.forgotPassword.setOnClickListener { resetPassword() }

        return binding.root
    }

    private fun login() {
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()
        if (email.isEmpty()) {
            binding.emailText.error = getString(R.string.empty_field)
            binding.emailText.requestFocus()
        } else if (password.length < 6) {
            binding.passwordText.error = getString(R.string.password_condition)
            binding.passwordText.requestFocus()
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setButtonClickable(false)
            checkPermissionAndLogin(email, password)
        } else {
            binding.emailText.error = getString(R.string.invalid_email)
            binding.emailText.requestFocus()
        }
    }

    private fun checkPermissionAndLogin(email: String, password: String) {
        firestore.collection("userTypes")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { query ->
                if (query.isEmpty) {
                    Toast.makeText(
                        context,
                        getString(R.string.no_permission),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    setButtonClickable(true)
                } else {
                    for (document in query) {
                        val userType = document.toObject(UserType::class.java)
                        if (userType.accountType == AccountType.VENDOR_OWNER) {
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        currentUser = firebaseAuth.currentUser!!
                                        Toast.makeText(
                                            context,
                                            getString(R.string.login_success),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        updateToken()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            getString(R.string.wrong_password),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        setButtonClickable(true)
                                    }
                                }
                        } else {
                            Toast.makeText(
                                context,
                                getString(R.string.no_permission),
                                Toast.LENGTH_LONG
                            )
                                .show()
                            setButtonClickable(true)
                        }
                    }
                }
            }
    }

    private fun resetPassword() {
        val email = binding.emailText.text.toString()
        if (email.isEmpty()) {
            binding.emailText.error = getString(R.string.empty_field)
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        getString(R.string.reset_password),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.invalid_email), Toast.LENGTH_LONG)
                        .show()
                }
        } else {
            binding.emailText.error = getString(R.string.invalid_email)
            binding.emailText.requestFocus()
        }
    }

    private fun navigateToRegisterFragment() {
        findNavController().navigate(R.id.registerFragment)
    }

    private fun updateToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { result ->
                Log.d("LoginFragment", "Current token: ${result.token}")
                firestore.collection("vendor_tokens")
                    .document(currentUser.uid)
                    .set(Token(result.token))
                    .addOnSuccessListener {
                        Log.d("LoginFragment", "Update token successful")
                        startMainActivity()
                    }
                    .addOnFailureListener {
                        Log.d("LoginFragment", it.toString())
                    }
            }
            .addOnFailureListener {
                Log.d("LoginFragment", it.toString())
                Toast.makeText(context, getString(R.string.error_occur), Toast.LENGTH_SHORT).show()
            }
    }

    private fun setButtonClickable(value: Boolean) {
        binding.loginButton.isClickable = value
        binding.createAccountButton.isClickable = value
    }

    private fun startMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}