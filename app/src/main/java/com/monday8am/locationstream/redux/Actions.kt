package com.monday8am.locationstream.redux

import com.monday8am.locationstream.data.Photo
import com.monday8am.locationstream.data.UserLocation
import org.rekotlin.Action

data class SetInitialContent(val photos: List<Photo>, val isUpdating: Boolean) : Action

data class AddNewPhoto(val photo: Photo) : Action

data class UpdatePhotoWithImage(val photo: Photo) : Action

data class StartStopUpdating(val isUpdating: Boolean): Action

data class NewLocationDetected(val location: UserLocation): Action
