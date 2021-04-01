package com.example.bk_foodcourt.order

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bk_foodcourt.order.orderList.HistoryOrderFragment
import com.example.bk_foodcourt.order.orderList.OngoingOrderFragment
import java.lang.IndexOutOfBoundsException


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fragment: Fragment, val userId: String) : FragmentStateAdapter(fragment){

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        0 to { OngoingOrderFragment(userId) },
        1 to { HistoryOrderFragment(userId) }
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}