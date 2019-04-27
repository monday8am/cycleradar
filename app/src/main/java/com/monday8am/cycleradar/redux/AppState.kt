package com.monday8am.cycleradar.redux

import com.monday8am.cycleradar.data.Photo
import com.monday8am.cycleradar.data.UserLocation
import org.rekotlin.StateType

data class AppState(val isGettingLocation: Boolean,
                    val lastLocationSaved: UserLocation?,
                    val photos: List<Photo>) : StateType