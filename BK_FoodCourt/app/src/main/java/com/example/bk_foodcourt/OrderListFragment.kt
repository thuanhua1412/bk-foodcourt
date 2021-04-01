package com.example.bk_foodcourt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderListFragment : Fragment() {
    var adapter: ItemAdapter? = null
    var orderList: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_order_list, container, false)
        orderList = view.findViewById(R.id.rvOrderList)
        orderList!!.setHasFixedSize(true)
        orderList!!.layoutManager = LinearLayoutManager(context)
        adapter = ItemAdapter(initData(), context!!)
        orderList!!.setAdapter(adapter)
        return view
    }

    private fun initData(): List<Row> {
        var oL: List<Row> = listOf(Row("1", "181"), Row("2", "191"), Row("3", "201"))
        return oL
    }
}