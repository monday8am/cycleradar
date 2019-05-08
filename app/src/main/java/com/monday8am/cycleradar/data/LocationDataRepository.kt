package com.monday8am.cycleradar.data


import com.google.firebase.firestore.FirebaseFirestore
import com.monday8am.cycleradar.SchedulerProvider
import com.monday8am.cycleradar.data.local.PreferencesHelper
import io.reactivex.Observable


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

    fun addNewLocation(location: UserLocation): Observable<Cyclist> {
        preferences.addLastLocation(location)
        return Observable.create<Cyclist> {
            mFirestore.collection("cyclists")
                .add(cyclist)
                .addOnSuccessListener { documentReference ->
                    documentReference.
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }

    }

    fun updateLocationFor(cyclist: Cyclist, location: UserLocation) {
        preferences.addLastLocation(location)
    }
}
