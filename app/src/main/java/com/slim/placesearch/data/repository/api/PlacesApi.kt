package com.slim.placesearch.data.repository.api

import com.slim.placesearch.data.model.response.PlacesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET("places/search")
    suspend fun getPlaces(
        @Query("ll") latLon : String
    ) : PlacesResponse


    companion object{
        const val BASE_URL = "https://api.foursquare.com/v3/"
    }

}