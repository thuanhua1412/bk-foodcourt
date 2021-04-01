package com.example.bk_foodcourt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TotalRevenueVendor_StatReport : AppCompatActivity() {
    val displayPiechart = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_revenue_vendor__stat_report)
        val displayChartButton : Button = findViewById(R.id.display_chart_button)
        displayChartButton.setOnClickListener{
            val intent = Intent(this, VendorPiechart::class.java)
            startActivityForResult(intent,displayPiechart)
        }

    }
}