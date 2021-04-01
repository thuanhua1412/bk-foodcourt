package com.example.bk_foodcourt

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Cook : AppCompatActivity() {
    private var mMainNav: BottomNavigationView? = null
    private var mMainFrame: FrameLayout? = null
    private var homeFragment: Fragment? = null
    private var orderListFragment: Fragment? = null
    private var notiFragment: Fragment? = null
    private var profileFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cook)
        mMainNav = findViewById(R.id.mainNav)
        mMainFrame = findViewById(R.id.mainFrame)
        homeFragment = HomeFragment()
        orderListFragment = OrderListFragment()
        notiFragment = NotificationFragment()
        profileFragment = ProfileFragment()
        setFragment(homeFragment)
        mMainNav!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    setFragment(homeFragment)
                    true
                }
                R.id.nav_orderlist -> {
                    setFragment(orderListFragment)
                    true
                }
                R.id.nav_profile -> {
                    setFragment(profileFragment)
                    true
                }
                else -> false
            }
        })
    }

    private fun setFragment(fragment: Fragment?) {
        val fragmentTransaction =
            supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame, fragment!!)
        fragmentTransaction.commit()
    }
}