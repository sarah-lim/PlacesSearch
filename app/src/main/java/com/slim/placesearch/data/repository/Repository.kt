package com.slim.placesearch.data.repository

import com.slim.placesearch.data.model.response.ApiResponse
import com.slim.placesearch.data.model.response.PlacesResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getPlaces(latLon : String) : Flow<ApiResponse<PlacesResponse>>
}