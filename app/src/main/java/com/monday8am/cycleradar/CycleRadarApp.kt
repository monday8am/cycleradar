package com.monday8am.cycleradar

import android.app.Application
import android.content.Context
import com.monday8am.cycleradar.data.PhotoDataRepository
import com.monday8am.cycleradar.data.local.PhotoDao
import com.monday8am.cycleradar.data.local.PhotosDatabase
import com.monday8am.cycleradar.data.local.PreferencesHelper
import com.monday8am.cycleradar.data.remote.RemoteWebService
import com.monday8am.cycleradar.redux.appStateReducer
import com.monday8am.cycleradar.redux.loggingMiddleware
import com.monday8am.cycleradar.redux.networkMiddleware
import org.rekotlin.Store
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val store = Store(
    reducer = ::appStateReducer,
    state = null,
    middleware = listOf(networkMiddleware, loggingMiddleware)
)

class CycleRadarApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = PhotoDataRepository(photoDao = providePhotoDataSource(applicationContext),
                                            webService = provideRemoteWebService(),
                                            preferences = providePreferences(applicationContext),
                                            scheduleProvider = AppSchedulerProvider())
    }

    companion object {
        @get:Synchronized lateinit var instance: CycleRadarApp
            private set

        var repository: PhotoDataRepository? = null
    }

    private fun providePreferences(context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }

    private fun providePhotoDataSource(context: Context): PhotoDao {
        val database = PhotosDatabase.getInstance(context)
        return database.photoDao()
    }

    private fun provideRemoteWebService(): RemoteWebService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create<RemoteWebService>(RemoteWebService::class.java)
    }
}
