package com.slim.placesearch.data.repository

import com.slim.placesearch.data.model.response.ApiResponse
import com.slim.placesearch.data.repository.api.PlacesApi
import com.slim.placesearch.data.model.response.PlacesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(private val placesApi: PlacesApi)
    : Repository {
    override suspend fun getPlaces(latLon : String): Flow<ApiResponse<PlacesResponse>> {
        return flow {
            val placesResponse = try {
                placesApi.getPlaces(latLon)
            }catch (e : Exception) {
                e.printStackTrace()
                emit(ApiResponse.Error(message = "Error getting places"))
                return@flow
            }

            emit(ApiResponse.Success(data = placesResponse))
        }
    }

}