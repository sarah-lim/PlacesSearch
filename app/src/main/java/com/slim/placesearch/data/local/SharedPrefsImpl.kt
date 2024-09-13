package com.slim.placesearch.data.local

import android.content.SharedPreferences

class SharedPrefsImpl (private val sharedPreferences : SharedPreferences): SharedPrefs{

    override fun saveLatitude(latitude: String) {
        sharedPreferences.edit().putString(LAT_KEY, latitude).apply()
    }

    override fun getLatitude(): String? {
        return sharedPreferences.getString(LAT_KEY, null)
    }

    override fun saveLongitude(longitude: String) {
        sharedPreferences.edit().putString(LON_KEY, longitude).apply()
    }

    override fun getLongitude(): String? {
        return sharedPreferences.getString(LON_KEY, null)
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val LAT_KEY = "latitude"
        private const val LON_KEY = "longitude"
    }
}