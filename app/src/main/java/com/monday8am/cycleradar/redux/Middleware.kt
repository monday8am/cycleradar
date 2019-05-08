package com.monday8am.cycleradar.redux

import android.util.Log
import com.monday8am.cycleradar.CycleRadarApp
import com.monday8am.cycleradar.data.Cyclist
import com.monday8am.cycleradar.data.UserLocation
import io.reactivex.disposables.Disposable
import org.rekotlin.DispatchFunction
import org.rekotlin.Middleware


private var savePhotoDisposable: Disposable? = null
private var getImageDisposable: Disposable? = null

internal val loggingMiddleware: Middleware<AppState> = { _, _ ->
    { next ->
        { action ->
            Log.d("New Action dispatched:", Thread.currentThread().id.toString() + "-" + action.toString())
            next(action)
        }
    }
}

internal val networkMiddleware: Middleware<AppState> = { dispatch, state ->
    { next ->
        { action ->
            when (action) {
                is NewLocationDetected -> {
                    val lastLocation = state()?.lastLocationSaved
                    if (action.location.isUseful(lastLocation = lastLocation)) {
                        saveUserLocation(action.location, dispatch)
                    }
                }
                is AddNewCyclist -> {
                    getImageForLocation(action.cyclist, dispatch)
                }
                is StartStopUpdating -> CycleRadarApp.repository?.setRequestingLocation(action.isUpdating)
            }
            next(action)
        }
    }
}

fun saveUserLocation(location: UserLocation, dispatch: DispatchFunction) {

}

fun getImageForLocation(cyclist: Cyclist, dispatch: DispatchFunction) {

}

