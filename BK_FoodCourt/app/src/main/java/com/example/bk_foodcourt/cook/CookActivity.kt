package com.example.bk_foodcourt.cook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.cook.menu.CookMenuFragmentDirections
import com.example.bk_foodcourt.databinding.CookActivityBinding
import com.example.bk_foodcourt.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class CookActivity : AppCompatActivity() {
    private lateinit var binding: CookActivityBinding
    private lateinit var viewModel: CookViewModel
    private var storeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CookActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(CookViewModel::class.java)

        val navController = this.findNavController(R.id.cook_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        binding.cookNav.setOnNavigationItemSelectedListener {
            bottomNavigationItemSelected(it)
        }

        viewModel.storeId.observe(this, Observer {
            it?.let {id ->
                    storeId = id
            }
        })
    }

    private fun bottomNavigationItemSelected(item: MenuItem): Boolean {
        val navController = this.findNavController(R.id.cook_nav_host_fragment)
        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.cookMenuFragment)
            }
            R.id.nav_order -> {
                val action = CookMenuFragmentDirections.actionCookMenuFragmentToCookOrderFragment(storeId)
                navController.navigate(action)
            }
            R.id.nav_logout -> {
                logout()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.cook_nav_host_fragment)
        return navController.navigateUp()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startLoginActivity()
    }

    private fun startLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}