package com.monday8am.cycleradar

import android.app.Application
import android.content.Context
import com.monday8am.cycleradar.data.LocationDataRepository
import com.monday8am.cycleradar.data.local.PreferencesHelper
import com.monday8am.cycleradar.redux.appStateReducer
import com.monday8am.cycleradar.redux.loggingMiddleware
import com.monday8am.cycleradar.redux.networkMiddleware
import org.rekotlin.Store

val store = Store(
    reducer = ::appStateReducer,
    state = null,
    middleware = listOf(networkMiddleware, loggingMiddleware)
)

class CycleRadarApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = LocationDataRepository(preferences = providePreferences(applicationContext),
                                            scheduleProvider = AppSchedulerProvider())
    }

    companion object {
        @get:Synchronized lateinit var instance: CycleRadarApp
            private set

        var repository: LocationDataRepository? = null
    }

    private fun providePreferences(context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }
}
