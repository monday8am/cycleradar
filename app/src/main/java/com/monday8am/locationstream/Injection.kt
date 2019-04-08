package com.monday8am.locationstream

import android.content.Context
import com.monday8am.locationstream.data.PhotoDataRepository
import com.monday8am.locationstream.data.local.PhotoDao
import com.monday8am.locationstream.data.local.PhotosDatabase
import com.monday8am.locationstream.data.local.PreferencesHelper
import com.monday8am.locationstream.data.remote.RemoteWebService
import com.monday8am.locationstream.ui.ViewModelFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Enables injection of data sources.
 */
object Injection {

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

    fun providePhotoDataRepository(context: Context): PhotoDataRepository {
        return PhotoDataRepository(photoDao = providePhotoDataSource(context),
                                   webService = provideRemoteWebService(),
                                   preferences = providePreferences(context),
                                   scheduleProvider = AppSchedulerProvider())
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val repository = providePhotoDataRepository(context)
        return ViewModelFactory(repository)
    }
}
