package com.monday8am.cycleradar.redux

import com.monday8am.cycleradar.data.Cyclist
import org.rekotlin.StateType

enum class LocationState {
    Stopped, Getting, Located
}

data class AppState(val isGettingLocation: LocationState,
                    val meCycling: Cyclist?,
                    val cyclists: List<Cyclist>) : StateType
