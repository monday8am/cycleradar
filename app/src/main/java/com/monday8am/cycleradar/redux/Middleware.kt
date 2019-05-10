package com.monday8am.cycleradar.redux

import android.util.Log
import com.monday8am.cycleradar.CycleRadarApp
import com.monday8am.cycleradar.data.Cyclist
import com.monday8am.cycleradar.data.UserLocation
import io.reactivex.disposables.Disposable
import org.rekotlin.DispatchFunction
import org.rekotlin.Middleware


private var saveLocationDisposable: Disposable? = null
private var getImageDisposable: Disposable? = null

internal val loggingMiddleware: Middleware<AppState> = { _, _ ->
    { next ->
        { action ->
            Log.d("New Action dispatched:", Thread.currentThread().id.toString() + "-" + action.toString())
            next(action)
        }
    }
}

internal val networkMiddleware: Middleware<AppState> = { dispatch, getState ->
    { next ->
        { action ->
            when (action) {
                is NewLocationDetected -> {
                    val meAsCyclist = getState()?.meCycling
                    if (action.location.isUseful(lastLocation = meAsCyclist?.location)) {
                        saveUserLocation(meAsCyclist?.id, action.location, dispatch)
                    }
                }
                is StartStopUpdating -> CycleRadarApp.repository?.setRequestingLocation(action.isUpdating)
            }
            next(action)
        }
    }
}

fun saveUserLocation(cyclistId: String?, newLocation: UserLocation, dispatch: DispatchFunction) {
    saveLocationDisposable = CycleRadarApp.repository
        ?.updateCyclist(cyclistId = cyclistId, location = newLocation)
        ?.subscribe { cyclist ->
            dispatch(UpdateMeAsCyclist(cyclist = cyclist))
        }
}

fun getImageForLocation(cyclist: Cyclist, dispatch: DispatchFunction) {

}

