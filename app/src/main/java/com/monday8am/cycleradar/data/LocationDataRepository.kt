package com.monday8am.cycleradar.data


import com.google.firebase.firestore.FirebaseFirestore
import com.monday8am.cycleradar.SchedulerProvider
import com.monday8am.cycleradar.data.local.PreferencesHelper


class LocationDataRepository(private val preferences: PreferencesHelper,
                             private val scheduleProvider: SchedulerProvider) {

    private val mFirestore = FirebaseFirestore.getInstance();

    fun isRequestingLocation(): Boolean {
        return preferences.requestingLocationUpdates()
    }

    fun getLastLocationSaved(): UserLocation? {
        return preferences.getLastLocation()
    }

    fun setRequestingLocation(value: Boolean) {
        preferences.setRequestingLocationUpdates(value)
    }

    fun addNewLocation(cyclist: Cyclist, location: UserLocation) {
        preferences.addLastLocation(location)
    }

    fun updateLocationFor(cyclist: Cyclist, location: UserLocation) {
        preferences.addLastLocation(location)
    }
}
