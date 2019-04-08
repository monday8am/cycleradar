package com.monday8am.locationstream.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo @JvmOverloads constructor(
    @ColumnInfo(name = "longitude") var longitude: Double,
    @ColumnInfo(name = "latitude")  var latitude: Double,
    @ColumnInfo(name = "imageUrl")  var imageUrl: String? = null,
    @ColumnInfo(name = "completed") var completed: Boolean = false,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "photoId") var photoId: Int = 0
)
