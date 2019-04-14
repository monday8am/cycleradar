package com.monday8am.locationstream

import android.app.Application
import android.content.Context
import com.monday8am.locationstream.data.PhotoDataRepository
import com.monday8am.locationstream.data.local.PhotoDao
import com.monday8am.locationstream.data.local.PhotosDatabase
import com.monday8am.locationstream.data.local.PreferencesHelper
import com.monday8am.locationstream.data.remote.RemoteWebService
import com.monday8am.locationstream.redux.appStateReducer
import com.monday8am.locationstream.redux.networkMiddleware
import org.rekotlin.Store
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val store = Store(
    reducer = ::appStateReducer,
    state = null,
    middleware = listOf(networkMiddleware)
)

class LocationApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = PhotoDataRepository(photoDao = providePhotoDataSource(applicationContext),
                                            webService = provideRemoteWebService(),
                                            preferences = providePreferences(applicationContext),
                                            scheduleProvider = AppSchedulerProvider())
    }

    companion object {
        @get:Synchronized lateinit var instance: LocationApp
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
