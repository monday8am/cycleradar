package com.monday8am.locationstream.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.monday8am.locationstream.data.UserLocation

class PreferencesHelper constructor(context: Context) {

    private val pref: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private val keyRequestingLocationUpdates = "requesting_location_updates"
    private val keyLastLongitude = "key_longitude"
    private val keyLastLatitude = "key_latitude"

    fun requestingLocationUpdates(): Boolean {
        return pref.getBoolean(keyRequestingLocationUpdates, false)
    }

    fun setRequestingLocationUpdates(requestingLocationUpdates: Boolean) {
        pref.edit().putBoolean(keyRequestingLocationUpdates, requestingLocationUpdates).apply()
    }

    fun addLastLocation(userLocation: UserLocation) {
        pref.edit().putFloat(keyLastLongitude, userLocation.longitude.toFloat()).apply()
        pref.edit().putFloat(keyLastLatitude, userLocation.latitude.toFloat()).apply()
    }

    fun getLastLocation(): UserLocation? {
        val longitude = pref.getFloat(keyLastLongitude, 0f)
        val latitude = pref.getFloat(keyLastLatitude, 0f)

        return if (longitude == 0f || latitude == 0f) {
            null
        } else {
            UserLocation(longitude.toDouble(), latitude.toDouble())
        }
    }
}
