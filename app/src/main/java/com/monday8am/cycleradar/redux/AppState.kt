package com.monday8am.cycleradar.redux

import com.monday8am.cycleradar.data.Cyclist
import org.rekotlin.StateType
import java.lang.Exception

enum class LocationState(val value: Int) {
    Stopped(0),
    Getting(1),
    Started(2);

    companion object {
        fun fromInt(value: Int): LocationState {
            return when (value) {
                0 -> Stopped
                1 -> Getting
                2 -> Started
                else -> throw Exception("Wrong value!")
            }
        }
    }
}

data class AppState(val isAppActive: Boolean,
                    val isGettingLocation: LocationState,
                    val meCycling: Cyclist?,
                    val cyclists: List<Cyclist>) : StateType
