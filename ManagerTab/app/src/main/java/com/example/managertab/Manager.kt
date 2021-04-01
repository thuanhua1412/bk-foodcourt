package com.example.managertab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class Manager() : AppCompatActivity() {
    private val openAddVendor = 1
    private val checkVendorInfos = 1
    private val removeVendor = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        // Open Add Remove Vendor from the corresponding Image Button and Button
        val addVendorImageButton: ImageButton = findViewById(R.id.addVendorButton)
        addVendorImageButton.setOnClickListener {
            openAddClick()
        }
        val addVendorButton : Button = findViewById(R.id.addRemoveButton)
        addVendorButton.setOnClickListener(){
            openAddClick()
        }
        // Open Top 5 Vendor List from the corresponding Image Button and Button
        val removeVendor : ImageButton = findViewById(R.id.removeVendorIB)
        removeVendor.setOnClickListener {
            openRemoveVendor()
        }
        val removeVendorButton : Button = findViewById(R.id.removeVendorButton)
        removeVendorButton.setOnClickListener {
            openRemoveVendor()
        }
        // Open Check Info Vendor from the corresponding Image Button and Button
        val checkVendorInfo : ImageButton = findViewById(R.id.vendorCheckInfo)
        checkVendorInfo.setOnClickListener{
            openCheckInfo()
        }
        val checkVendorsInfoButton : Button = findViewById(R.id.vendor_check_Button)
        checkVendorsInfoButton.setOnClickListener{
            openCheckInfo()
        }
    }

    private fun openAddClick() {
        val intent = Intent(this, AddNewVendorActivity::class.java)
        startActivityForResult(intent, openAddVendor)
    }
    private fun openCheckInfo(){
        val intent = Intent(this, CheckVendorInfosActivity::class.java)
        startActivityForResult(intent,checkVendorInfos)
    }

    private fun openRemoveVendor(){
        val intent = Intent(this,RemoveVendorActivity::class.java)
        startActivityForResult(intent,removeVendor)
    }

}

