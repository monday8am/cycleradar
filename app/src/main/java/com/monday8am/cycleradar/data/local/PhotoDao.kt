package com.monday8am.cycleradar.data.local


import androidx.room.*
import com.monday8am.cycleradar.data.Photo
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PhotoDao {

    @Query("SELECT * FROM Photos ORDER BY photoId DESC")
    fun getPhotos(): Flowable<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(photo: Photo): Single<Long>

    @Update
    fun updatePhoto(photo: Photo): Completable

}
