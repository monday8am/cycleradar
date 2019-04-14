package com.monday8am.locationstream.redux

import com.monday8am.locationstream.data.Photo
import com.monday8am.locationstream.data.UserLocation
import org.rekotlin.StateType

data class AppState(val isGettingLocation: Boolean,
                    val lastLocationSaved: UserLocation?,
                    val photos: List<Photo>) : StateType
