package com.monday8am.cycleradar.redux

import com.monday8am.cycleradar.data.Cyclist
import org.rekotlin.StateType

data class AppState(val isGettingLocation: Boolean,
                    val meCycling: Cyclist?,
                    val cyclists: List<Cyclist>) : StateType
