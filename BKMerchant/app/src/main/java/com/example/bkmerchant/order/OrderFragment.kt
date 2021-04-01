package com.example.bkmerchant.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.OrderFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class OrderFragment : Fragment() {
    private lateinit var binding: OrderFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.orders)
        val args = OrderFragmentArgs.fromBundle(requireArguments())

        binding = OrderFragmentBinding.inflate(inflater, container, false)

        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter =
            SectionsPagerAdapter(this, args.storeID)

        TabLayoutMediator(tabLayout, viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.text = getTabTitle(position)
        }.attach()

        return binding.root
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            0 -> getString(R.string.tab_text_1)
            1 -> getString(R.string.tab_text_2)
            else -> null
        }
    }
}