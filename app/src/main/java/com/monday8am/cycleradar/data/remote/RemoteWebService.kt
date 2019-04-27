package com.monday8am.cycleradar.data.remote

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


interface RemoteWebService {

    @GET("/services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1&extras=url_c")
    fun listPhotos(@Query("api_key") apiKey: String,
                   @Query("lon") longitude: Double,
                   @Query("lat") latitude: Double,
                   @Query("radius") radius: Float = 0.1f,
                   @Query("per_page") maxNumberOfPhotos: Int = 1): Observable<PhotosSearchResponse>

}
