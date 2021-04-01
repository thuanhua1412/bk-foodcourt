package com.example.bk_foodcourt.account

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.bk_foodcourt.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}