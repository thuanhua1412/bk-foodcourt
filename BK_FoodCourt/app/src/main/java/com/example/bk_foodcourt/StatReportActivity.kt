package com.example.bk_foodcourt


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class StatReportActivity : AppCompatActivity() {
    private val totalRevenueVendor = 2
    private val totalRevenueItem = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat_report)
        val vendorButton: ImageButton = findViewById(R.id.Vendor_Chart_Button)
        val itemButton: ImageButton = findViewById(R.id.Item_Chart_Button)
        val vendorTextButton: Button = findViewById(R.id.vendor_text_button)
        val itemTextButton : Button = findViewById(R.id.vendor_item_button)
        vendorButton.setOnClickListener{
            openTotalRevenuesVendor()
        }
        vendorTextButton.setOnClickListener{
            openTotalRevenuesVendor()
        }
        itemButton.setOnClickListener{
            openTotalRevenuesItem()
        }
        itemTextButton.setOnClickListener{
            openTotalRevenuesItem()
        }
    }
    private fun openTotalRevenuesVendor() {
        val intent = Intent(this, TotalRevenueVendor_StatReport::class.java)
        startActivityForResult(intent,totalRevenueVendor)
    }
    private fun openTotalRevenuesItem(){
        val intent = Intent(this,TotalRevenueItem_StatReport::class.java)
        startActivityForResult(intent,totalRevenueItem)
    }

}