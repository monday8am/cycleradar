package com.monday8am.cycleradar.redux

import org.rekotlin.Action


fun appStateReducer(action: Action, state: AppState?): AppState {
    var newState = state ?: getInitialState()

    when (action) {
        is SetInitialContent -> {
            newState = newState.copy(isGettingLocation = action.isUpdating)
        }
        is StartStopUpdating -> {
            newState = if (action.isUpdating) {
                newState.copy(isGettingLocation = LocationState.Getting)
            } else {
                newState.copy(isGettingLocation = LocationState.Stopped)
            }
        }
        is UpdateMeAsCyclist -> {
            newState = newState.copy(meCycling = action.cyclist, isGettingLocation = LocationState.Started)
        }
        is UpdateCyclists -> {
            val mutableList = action.allCyclists.toMutableList()
            mutableList.removeAll { it.id == newState.meCycling?.id }
            newState = newState.copy(cyclists = mutableList)
        }
        is DeleteMe -> {
            newState = newState.copy(meCycling = null)
        }
    }

    return newState
}

private fun getInitialState(): AppState {
    return AppState(
        isAppActive = true,
        isGettingLocation = LocationState.Stopped,
        meCycling = null,
        cyclists = listOf()
    )
}
