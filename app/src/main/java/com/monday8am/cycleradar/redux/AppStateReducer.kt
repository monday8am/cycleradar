package com.monday8am.cycleradar.redux

import org.rekotlin.Action


fun appStateReducer(action: Action, state: AppState?): AppState {
    var newState = state ?: getInitialState()

    when (action) {
        is SetInitialContent -> {
            newState = newState.copy(isGettingLocation = action.isUpdating)
        }
        is StartStopUpdating -> newState = newState.copy(isGettingLocation = action.isUpdating)
        is UpdateMeAsCyclist -> {
            val mutablePhotos = newState.cyclists.toMutableList()
            mutablePhotos.add(action.cyclist)
            newState = newState.copy(meCycling = action.cyclist)
        }
        is UpdateCyclists -> {
            newState = newState.copy(cyclists = action.allCyclists)
        }
    }

    return newState
}

private fun getInitialState(): AppState {
    return AppState(
        isGettingLocation = false,
        meCycling = null,
        cyclists = listOf()
    )
}
