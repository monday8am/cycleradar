package com.monday8am.locationstream.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.monday8am.locationstream.data.Photo


@Database(entities = [Photo::class], version = 1)
abstract class PhotosDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    companion object {

        private var instance: PhotosDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): PhotosDatabase {
            synchronized(lock) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        PhotosDatabase::class.java, "Photos.db")
                        .build()
                }
                return instance!!
            }
        }
    }
}
