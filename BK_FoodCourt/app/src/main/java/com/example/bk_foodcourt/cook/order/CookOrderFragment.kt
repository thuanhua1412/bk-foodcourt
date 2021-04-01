package com.example.bk_foodcourt.cook.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bk_foodcourt.R
import com.example.bk_foodcourt.databinding.OrderFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CookOrderFragment() : Fragment() {
    private lateinit var binding: OrderFragmentBinding
    private lateinit var storeId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.orders)

        val args = CookOrderFragmentArgs.fromBundle(requireArguments())
        storeId = args.storeId

        binding = OrderFragmentBinding.inflate(inflater, container, false)

        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter =
            CookPagerAdapter(this, storeId)

        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.text = getTabTitle(position)
        }.attach()

        return binding.root
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            0 -> getString(R.string.new_order)
            1 -> getString(R.string.my_order)
            else -> null
        }
    }
}