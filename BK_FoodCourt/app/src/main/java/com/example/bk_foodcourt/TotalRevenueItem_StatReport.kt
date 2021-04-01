package com.example.bk_foodcourt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TotalRevenueItem_StatReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val displayPiechartItem = 1
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_revenue_item__stat_report)
        val displayItemChartButton : Button = findViewById(R.id.display_item_chart_button)
        displayItemChartButton.setOnClickListener{
            val intent = Intent(this, ItemPieChart::class.java)
            startActivityForResult(intent,displayPiechartItem)
        }

    }

}