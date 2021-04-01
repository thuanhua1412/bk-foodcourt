package com.example.bkmerchant.order

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bkmerchant.R
import com.example.bkmerchant.order.orderList.HistoryOrderFragment
import com.example.bkmerchant.order.orderList.OngoingOrderFragment
import java.lang.IndexOutOfBoundsException

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fragment: Fragment, val storeId: String) : FragmentStateAdapter(fragment){

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        0 to { OngoingOrderFragment(storeId) },
        1 to { HistoryOrderFragment(storeId) }
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}