package com.monday8am.locationstream.ui


import androidx.lifecycle.ViewModel
import com.monday8am.locationstream.data.Photo
import com.monday8am.locationstream.data.PhotoDataRepository
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject


class PhotoListViewModel(private val repository: PhotoDataRepository) : ViewModel() {

    fun getPhotos(): Flowable<List<Photo>> {
        return repository.getPhotos()
    }

    fun isRequestingLocation(): BehaviorSubject<Boolean> {
        return repository.isRequestingLocationSignal()
    }

    fun startStopRequestingLocation(value: Boolean) {
        repository.setRequestingLocation(value)
    }
}
