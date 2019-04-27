package com.monday8am.cycleradar.redux

import com.monday8am.cycleradar.data.Photo
import com.monday8am.cycleradar.data.UserLocation
import org.rekotlin.Action

data class SetInitialContent(val photos: List<Photo>,
                             val isUpdating: Boolean,
                             val lastLocation: UserLocation?) : Action

data class AddNewPhoto(val photo: Photo, val location: UserLocation) : Action

data class UpdatePhotoWithImage(val photo: Photo) : Action

data class StartStopUpdating(val isUpdating: Boolean): Action

data class NewLocationDetected(val location: UserLocation): Action
