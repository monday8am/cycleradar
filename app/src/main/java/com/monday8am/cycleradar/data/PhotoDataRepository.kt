package com.monday8am.cycleradar.data


import com.monday8am.cycleradar.SchedulerProvider
import com.monday8am.cycleradar.data.local.PhotoDao
import com.monday8am.cycleradar.data.local.PreferencesHelper
import com.monday8am.cycleradar.data.remote.PhotosSearchResponse
import com.monday8am.cycleradar.data.remote.RemoteWebService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single


class PhotoDataRepository(private val photoDao: PhotoDao,
                          private val preferences: PreferencesHelper,
                          private val webService: RemoteWebService,
                          private val scheduleProvider: SchedulerProvider) {

    fun isRequestingLocation(): Boolean {
        return preferences.requestingLocationUpdates()
    }

    fun getLastLocationSaved(): UserLocation? {
        return preferences.getLastLocation()
    }

    fun setRequestingLocation(value: Boolean) {
        preferences.setRequestingLocationUpdates(value)
    }

    fun getPhotos(): Flowable<List<Photo>> {
        return photoDao.getPhotos()
            .subscribeOn(scheduleProvider.io())
    }

    fun addPhotoFromLocation(location: UserLocation): Single<Photo> {
        preferences.addLastLocation(location)

        val newPhoto = Photo(longitude = location.longitude,
                             latitude = location.latitude)

        return photoDao.insertPhoto(newPhoto)
            .subscribeOn(scheduleProvider.io())
            .map { photoId -> newPhoto.copy(photoId = photoId.toInt()) }
    }

    fun getRemoteImageFor(longitude: Double, latitude: Double): Observable<String> {
        return webService.listPhotos("2e543b92f08024a1b91b274f75727416",
            longitude = longitude,
            latitude = latitude)
            .subscribeOn(scheduleProvider.io())
            .onErrorReturnItem(PhotosSearchResponse(null,null))
            .map { result ->
                result.photos?.photo?.first()?.url_c ?: ""
            }
    }

    fun updatePhotoWithImage(photo: Photo, image: String): Completable {
        return photoDao.updatePhoto(photo.copy(imageUrl = image))
    }
}
