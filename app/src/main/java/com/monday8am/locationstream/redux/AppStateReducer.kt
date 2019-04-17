package com.monday8am.locationstream.redux

import org.rekotlin.Action


fun appStateReducer(action: Action, state: AppState?): AppState {
    var newState = state ?: getInitialState()

    when (action) {
        is SetInitialContent -> {
            newState = newState.copy(isGettingLocation = action.isUpdating, photos = action.photos)
        }
        is StartStopUpdating -> newState = newState.copy(isGettingLocation = action.isUpdating)
        is AddNewPhoto -> {
            val mutablePhotos = newState.photos.toMutableList()
            mutablePhotos.add(action.photo)
            newState = newState.copy(photos = mutablePhotos, lastLocationSaved = action.location)
        }
        is UpdatePhotoWithImage -> {
            val index = newState.photos.indexOfFirst { it.photoId == action.photo.photoId }
            if (index != -1) {
                val mutablePhotos = newState.photos.toMutableList()
                mutablePhotos[index] = action.photo
                newState = newState.copy(photos = mutablePhotos)
            }
        }
    }

    return newState
}

private fun getInitialState(): AppState {
    return AppState(
        isGettingLocation = false,
        lastLocationSaved = null,
        photos = listOf()
    )
}
