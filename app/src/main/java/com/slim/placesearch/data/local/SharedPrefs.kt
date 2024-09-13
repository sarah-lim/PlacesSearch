package com.slim.placesearch.data.local

interface SharedPrefs {

    fun saveLatitude(latitude: String)

    fun getLatitude() : String?

    fun saveLongitude(longitude: String)

    fun getLongitude() : String?

    fun clear()
}