package com.monday8am.cycleradar.redux

import android.util.Log
import com.monday8am.cycleradar.CycleRadarApp
import com.monday8am.cycleradar.data.UserLocation
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.rekotlin.DispatchFunction
import org.rekotlin.Middleware
import org.rekotlin.ReKotlinInit
import java.util.concurrent.TimeUnit


private var saveLocationDisposable: Disposable? = null
private var getCyclistListDisposable: Disposable? = null
private var deleteDisposable: Disposable? = null
private var updatePeriod: Long = 10

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
                is ReKotlinInit -> {
                    getRemoteListStream(dispatch)
                }
                is NewLocationDetected -> {
                    val meAsCyclist = getState()?.meCycling
                    if (action.location.isUseful(lastLocation = meAsCyclist?.location)) {
                        updateCyclistLocation(meAsCyclist?.id, action.location, dispatch)
                    }

                    if (getState()?.isAppActive == true) {

                    }
                }
                is StartStopUpdating -> {
                    val myId = getState()?.meCycling?.id

                    if (!action.isUpdating && myId != null) {
                        deleteCyclist(myId, dispatch)
                    }

                    if (getState()?.isAppActive == true) {
                        getRemoteListStream(dispatch)
                    }
                }
            }
            next(action)
        }
    }
}

fun updateCyclistLocation(cyclistId: String?, newLocation: UserLocation, dispatch: DispatchFunction) {
    saveLocationDisposable = CycleRadarApp.repository
        ?.updateCyclist(cyclistId = cyclistId, location = newLocation)
        ?.subscribe { cyclist ->
            dispatch(UpdateMeAsCyclist(cyclist = cyclist))
        }
}

fun getAllCyclists(dispatch: DispatchFunction) {
    val repository = CycleRadarApp.repository ?: return

    getCyclistListDisposable?.dispose()
    getCyclistListDisposable = repository.getAllCyclists()
        .subscribe { result ->
            dispatch(UpdateCyclists(allCyclists = result))
        }
}

fun getRemoteListStream(dispatch: DispatchFunction) {
    val repository = CycleRadarApp.repository ?: return

    getCyclistListDisposable?.dispose()
    getCyclistListDisposable = Observable.interval(updatePeriod, TimeUnit.SECONDS)
        .mergeWith(Observable.just(Long.MIN_VALUE))
        .flatMap { repository.getAllCyclists() }
        .subscribe { result ->
            dispatch(UpdateCyclists(allCyclists = result))
        }
}


fun deleteCyclist(cyclistId: String, dispatch: DispatchFunction) {
    val repository = CycleRadarApp.repository ?: return

    deleteDisposable = repository
        .deleteCyclist(cyclistId = cyclistId)
        .subscribe { deletedId ->
            dispatch(DeleteMe(cyclistId = deletedId))
        }
}
