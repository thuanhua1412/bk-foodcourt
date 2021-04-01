package com.example.bkmerchant.report

import android.os.Bundle
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.ReportFragmentBinding
import com.example.bkmerchant.order.Order
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.String.format
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment: Fragment() {
    private lateinit var binding: ReportFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.report)

        binding = ReportFragmentBinding.inflate(inflater, container, false)

        makeReport()

        return  binding.root
    }

    private fun makeReport() {
        var processingOrder = 0
        var finishOrder = 0
        var cancelOrder = 0
        var totalSale = 0.0

        val curDateString = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(Date())
        val curDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).parse(curDateString)!!

        val curTimestamp = Timestamp(curDate)

        FirebaseFirestore.getInstance()
            .collection("order")
            .whereGreaterThanOrEqualTo("time", curTimestamp)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val order = document.toObject(Order::class.java)
                    if (order.status == 3) {
                        finishOrder += 1
                        totalSale += order.total
                    } else if (order.status == 4) {
                        cancelOrder += 1
                    } else {
                        processingOrder += 1
                        totalSale += order.total
                    }
                }
                var text = getString(R.string.num_process_order, processingOrder) + "\n"
                text += getString(R.string.num_finish_order, finishOrder) + "\n"
                text += getString(R.string.num_cancel_order, finishOrder) + "\n\n"
                text += getString(R.string.today_total_sales, String.format("%1$,.0f", totalSale) + "đ")
                binding.reportText.text = text
            }

    }
}