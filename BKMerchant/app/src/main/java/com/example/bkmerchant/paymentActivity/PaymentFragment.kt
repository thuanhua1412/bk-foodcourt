package com.example.bkmerchant.paymentActivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bkmerchant.MainActivity
import com.example.bkmerchant.R
import com.example.bkmerchant.accountActivity.AccountActivity
import com.example.bkmerchant.databinding.PaymentFragmentBinding
import com.example.bkmerchant.storeActivity.StoreActivity

class PaymentFragment: Fragment() {
    private lateinit var binding: PaymentFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = PaymentFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.bottomNav.selectedItemId = R.id.nav_payment
        binding.bottomNav.setOnNavigationItemSelectedListener {
            bottomNavigationItemSelected(it)
        }

        return binding.root
    }

    private fun bottomNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
//                val intent = Intent(context, MainActivity::class.java)
//                startActivity(intent)
                activity?.finish()
            }
            R.id.nav_account -> {
                val intent = Intent(context, AccountActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            R.id.nav_store -> {
                val intent = Intent(context, StoreActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
        return true
    }
}