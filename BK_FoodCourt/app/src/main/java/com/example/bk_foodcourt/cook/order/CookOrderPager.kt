package com.example.bk_foodcourt.cook.order

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bk_foodcourt.cook.order.orderList.CookMyOrderFragment
import com.example.bk_foodcourt.cook.order.orderList.CookNewOrderFragment
import java.lang.IndexOutOfBoundsException

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class CookPagerAdapter(fragment: Fragment, val storeId: String) : FragmentStateAdapter(fragment){

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        0 to { CookNewOrderFragment(storeId) },
        1 to { CookMyOrderFragment() }
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}