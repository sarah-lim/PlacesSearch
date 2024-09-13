package com.slim.placesearch.data.model.response

import com.slim.placesearch.data.model.Context
import com.slim.placesearch.data.model.Result

data class PlacesResponse(
    val context: Context,
    val results: List<Result>
)