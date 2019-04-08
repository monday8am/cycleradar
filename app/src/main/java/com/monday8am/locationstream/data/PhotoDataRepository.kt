package com.monday8am.locationstream.data


import com.monday8am.locationstream.SchedulerProvider
import com.monday8am.locationstream.data.local.PhotoDao
import com.monday8am.locationstream.data.local.PreferencesHelper
import com.monday8am.locationstream.data.remote.PhotosSearchResponse
import com.monday8am.locationstream.data.remote.RemoteWebService
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject


class PhotoDataRepository(private val photoDao: PhotoDao,
                          private val preferences: PreferencesHelper,
                          private val webService: RemoteWebService,
                          private val scheduleProvider: SchedulerProvider) {

    private val isRequestingSignal: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private var lastLocation: UserLocation? = null

    init {
        isRequestingSignal.onNext(preferences.requestingLocationUpdates())
        lastLocation = preferences.getLastLocation()
    }

    fun isRequestingLocation(): Boolean {
        return preferences.requestingLocationUpdates()
    }

    fun isRequestingLocationSignal(): BehaviorSubject<Boolean> {
        return isRequestingSignal
    }

    fun setRequestingLocation(value: Boolean) {
        preferences.setRequestingLocationUpdates(value)
        isRequestingSignal.onNext(value)
    }

    fun getPhotos(): Flowable<List<Photo>> {
        return photoDao.getPhotos()
            .subscribeOn(scheduleProvider.io())
    }

    fun addPhotoFromLocation(location: UserLocation): Single<Long> {
        if (!location.isUseful(lastLocation)) {
            return Single.just(-1)
        }

        preferences.addLastLocation(location)
        lastLocation = location

        val newPhoto = Photo(longitude = location.longitude,
                             latitude = location.latitude)

        return photoDao.insertPhoto(newPhoto)
            .subscribeOn(scheduleProvider.io())
            .doAfterSuccess { updatePhotoWithImage(newPhoto, it) }
    }

    private fun updatePhotoWithImage(photo: Photo, index: Long) {
        webService.listPhotos("2e543b92f08024a1b91b274f75727416",
                              longitude = photo.longitude,
                              latitude = photo.latitude)
            .subscribeOn(scheduleProvider.io())
            .onErrorReturnItem(PhotosSearchResponse(null,null))
            .flatMap { result ->
                val updated = photo.copy(photoId = index.toInt(), imageUrl = result.photos?.photo?.first()?.url_c, completed = true)
                return@flatMap photoDao.updatePhoto(updated)
                                       .toObservable<Boolean>()
            }
            .subscribe()
    }
}
