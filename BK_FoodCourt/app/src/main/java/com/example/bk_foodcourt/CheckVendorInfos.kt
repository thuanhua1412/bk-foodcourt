package com.example.bk_foodcourt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class CheckVendorInfos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_vendor_infos)
        val vendor1Button : ImageButton = findViewById(R.id.vendor1_onclick)
        val vendor2Button : ImageButton = findViewById(R.id.vendor2_onclick)
        val vendor3Button : ImageButton = findViewById(R.id.vendor3_onclick)
        val vendor4Button : ImageButton = findViewById(R.id.vendor4_onclick)
        val vendor5Button : ImageButton = findViewById(R.id.vendor5_onclick)

    }
}