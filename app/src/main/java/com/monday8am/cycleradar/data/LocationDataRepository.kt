package com.monday8am.cycleradar.data


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.monday8am.cycleradar.SchedulerProvider
import com.monday8am.cycleradar.data.local.PreferencesHelper
import io.reactivex.Observable
import java.util.*


class LocationDataRepository(private val preferences: PreferencesHelper,
                             private val scheduleProvider: SchedulerProvider) {

    private val collectionPath = "cyclist"
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
            mFirestore.collection(collectionPath)
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

    fun getAllCyclists(): Observable<List<Cyclist>> {
        return Observable.create<List<Cyclist>> {
            mFirestore.collection(collectionPath)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d(tag, "DocumentSnapshot containing: ${documents.count()} elements.")
                    val cyclists = documents.map { Cyclist(longitude = it.getDouble("longitude") ?: 0.0,
                                                           latitude = it.getDouble("latitude") ?: 0.0,
                                                           id = it.getString("id") ?: "") }
                    it.onNext(cyclists)
                    it.onComplete()
                }
                .addOnFailureListener { e ->
                    Log.w(tag, "Error adding document", e)
                    it.onError(e)
                }
        }
    }
}
