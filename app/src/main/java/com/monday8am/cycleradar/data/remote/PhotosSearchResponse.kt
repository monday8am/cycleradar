package com.monday8am.cycleradar.data.remote


data class PhotosSearchResponse(val photos: PhotoContainer?, val stat: String?)

data class PhotoContainer(val page: Int?,
                          val pages: Int?,
                          val perpage: Int?,
                          val total: Int?,
                          val photo: List<FlickrPhoto>?)

data class FlickrPhoto(val id: String?,
                       val owner: String?,
                       val secret: String?,
                       val server: String?,
                       val farm: Int?,
                       val title: String?,
                       val ispublic: Int?,
                       val isfriend: Int?,
                       val isfamily: Int?,
                       val url_c: String?,
                       val height_c: String?,
                       val width_c: String?)
