package com.monday8am.cycleradar.data


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.monday8am.cycleradar.SchedulerProvider
import com.monday8am.cycleradar.data.local.PreferencesHelper
import io.reactivex.Observable
import java.util.*


class LocationDataRepository(private val preferences: PreferencesHelper,
                             private val scheduleProvider: SchedulerProvider) {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val tag = "LocationDataRepository"

    fun isRequestingLocation(): Boolean {
        return preferences.requestingLocationUpdates()
    }

    fun getLastLocationSaved(): UserLocation? {
        return preferences.getLastLocation()
    }

    fun setRequestingLocation(value: Boolean) {
        preferences.setRequestingLocationUpdates(value)
    }

    fun updateCyclist(cyclistId: String?, location: UserLocation): Observable<Cyclist> {
        preferences.addLastLocation(location)

        val cyclist = Cyclist(latitude = location.latitude,
                              longitude = location.longitude,
                              id = cyclistId ?: UUID.randomUUID().toString())

        return Observable.create<Cyclist> {
            mFirestore.collection("cyclists")
                .document(cyclist.id)
                .set(cyclist.toHash())
                .addOnSuccessListener { documentReference ->
                    Log.d(tag, "DocumentSnapshot added with ID: $documentReference")
                    it.onNext(cyclist)
                    it.onComplete()
                }
                .addOnFailureListener { e ->
                    Log.w(tag, "Error adding document", e)
                    it.onError(e)
                }
        }
    }
}
