package com.monday8am.cycleradar.redux

import com.monday8am.cycleradar.data.Cyclist
import com.monday8am.cycleradar.data.UserLocation
import org.rekotlin.Action

data class SetInitialContent(val isUpdating: Boolean,
                             val lastLocation: UserLocation?) : Action

data class UpdateMeAsCyclist(val cyclist: Cyclist) : Action

data class StartStopUpdating(val isUpdating: Boolean): Action

data class NewLocationDetected(val location: UserLocation): Action
