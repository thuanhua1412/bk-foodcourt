package com.example.bkmerchant.accountActivity

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.example.bkmerchant.R
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.setting)

        val sharedPreferences = preferenceScreen.sharedPreferences
        val prefScreen: PreferenceScreen = preferenceScreen
        val count: Int = prefScreen.preferenceCount
        for (i in 0 until count) {
            val p: Preference = prefScreen.getPreference(i)
            if (p !is CheckBoxPreference) {
                val value = sharedPreferences.getString(p.key, "")
                setPreferenceSummary(p, value!!)
            }
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: Any) {
        val stringValue = value.toString()
        Log.d("SettingsFragment", stringValue)
        if (preference is ListPreference) {
            val listPreference: ListPreference = preference
            val prefIndex: Int = listPreference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.entries[prefIndex])
            }
        } else {
            preference.summary = stringValue
        }
    }

    @Suppress("Deprecation")
    private fun changeLanguageLocale(localeCode: String) {
        val newLocale = Locale(localeCode)
        val res = resources
        val displayMetrics = res.displayMetrics
        val config = res.configuration

        config.setLocale(newLocale)
        res.updateConfiguration(config, displayMetrics)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String
    ) {
        val preference: Preference? = findPreference(key)
        if (null != preference) {
            if (preference !is CheckBoxPreference) {
                val value = sharedPreferences.getString(key, "")!!
                Log.d("SettingsFragment", value)
                setPreferenceSummary(preference, value)
                if (key == getString(R.string.language)) {
                    changeLanguageLocale(value)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}