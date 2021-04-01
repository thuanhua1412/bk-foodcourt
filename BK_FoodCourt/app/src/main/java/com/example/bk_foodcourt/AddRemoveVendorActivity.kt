package com.example.bk_foodcourt

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI


class AddRemoveVendorActivity : AppCompatActivity() {
    private var viewPager: ViewPager2? = null
    private var storage: FirebaseStorage? = null
    private var storageReference : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_remove_vendor)
        viewPager = findViewById<ViewPager2>(R.id.pager)
        //Init firebaseCr
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        //Setup button

        viewPager!!.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return if (position == 0) {
                    AddVendorFragment.newInstance()
                } else {
                    RemoveVendorFragment.newInstance()
                }
            }

            override fun getItemCount(): Int {
                return 2
            }
        }
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager!!) { tab, position ->
            if (position == 0) {
                tab.text = "Add New Vendor"
            } else {
                tab.text = "Remove a Vendor"
            }
        }.attach()
    }

}

class AddVendorFragment : Fragment() {
    private val pickImageRequest : Int = 1
    private var vendorImageView : ImageView? = null
    private var chooseImageButton : Button? = null
    private var text : EditText? = null
    private var vendorImageUri : Uri? =null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View =  inflater!!.inflate(R.layout.fragment_add_vendor, container, false)

        chooseImageButton = view.findViewById(R.id.chooseVendorImage)
        vendorImageView = view.findViewById(R.id.vendorImageView)
        val progressBar : ProgressBar = view.findViewById(R.id.progress_image)
        text = view.findViewById(R.id.VendorNameEditText)
        chooseImageButton!!.setOnClickListener{
            openFileChooser()
        }
        return inflater.inflate(R.layout.fragment_add_vendor, container, false)
    }
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), pickImageRequest)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageRequest && resultCode == Activity.RESULT_OK && data != null && data.data != null
        ) {
            vendorImageUri = data.data!!
            vendorImageView!!.setImageURI(vendorImageUri)
        }
    }
    companion object {
        fun newInstance(): AddVendorFragment = AddVendorFragment()
    }
}

class RemoveVendorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_remove_vendor, container, false)
    }

    companion object {
        fun newInstance(): RemoveVendorFragment = RemoveVendorFragment()
    }
}
