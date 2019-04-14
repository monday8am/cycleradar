package com.monday8am.locationstream.redux

import com.monday8am.locationstream.LocationApp
import com.monday8am.locationstream.data.Photo
import com.monday8am.locationstream.data.UserLocation
import io.reactivex.disposables.Disposable
import org.rekotlin.DispatchFunction
import org.rekotlin.Middleware


private var savePhotoDisposable: Disposable? = null
private var getImageDisposable: Disposable? = null

internal val networkMiddleware: Middleware<AppState> = { dispatch, state ->
    { next ->
        { action ->
            when (action) {
                is NewLocationDetected -> savePhotoForLocation(action.location, dispatch)
                is AddNewPhoto -> getImageForLocation(action.photo, dispatch)
                is StartStopUpdating -> LocationApp.repository?.setRequestingLocation(action.isUpdating)
            }
            next(action)
        }
    }
}

fun savePhotoForLocation(location: UserLocation, dispatch: DispatchFunction) {
    savePhotoDisposable = LocationApp.repository?.addPhotoFromLocation(location = location)
                                                ?.subscribe { photo ->
                                                    if (photo != null) {
                                                        dispatch(AddNewPhoto(photo))
                                                    }
                                                }
}

fun getImageForLocation(photo: Photo, dispatch: DispatchFunction) {
    getImageDisposable = LocationApp.repository?.updatePhotoWithImage(photo = photo)
                                               ?.subscribe { updatedPhoto ->
                                                   dispatch(UpdatePhotoWithImage(updatedPhoto))
                                               }
}
