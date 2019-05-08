package com.monday8am.cycleradar.redux

import org.rekotlin.Action


fun appStateReducer(action: Action, state: AppState?): AppState {
    var newState = state ?: getInitialState()

    when (action) {
        is SetInitialContent -> {
            newState = newState.copy(isGettingLocation = action.isUpdating, lastLocationSaved = action.lastLocation)
        }
        is StartStopUpdating -> newState = newState.copy(isGettingLocation = action.isUpdating)
        is AddNewCyclist -> {
            val mutablePhotos = newState.cyclists.toMutableList()
            mutablePhotos.add(action.cyclist)
            newState = newState.copy(cyclists = mutablePhotos)
        }
        is UpdateLocationFor -> {
            /*
            val index = newState.cyclists.indexOfFirst { it.cyclistId == action.cyclist.cyclistId }
            if (index != -1) {
                val mutablePhotos = newState.cyclists.toMutableList()
                mutablePhotos[index] = action.cyclist
                newState = newState.copy(cyclists = mutablePhotos)
            }
            */
        }
    }

    return newState
}

private fun getInitialState(): AppState {
    return AppState(
        isGettingLocation = false,
        lastLocationSaved = null,
        cyclists = listOf()
    )
}
