package com.slim.placesearch.data.model.response

sealed class ApiResponse<T>(
    val data : T? = null,
    val message : String? = null
) {
    class Error<T> (data: T? = null, message: String?) : ApiResponse<T>(data, message)
    class Success<T> (data: T) : ApiResponse<T>(data)

}