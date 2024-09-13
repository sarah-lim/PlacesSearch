package com.slim.placesearch

import com.slim.placesearch.data.local.PlaceUI

data class PlaceSearchState(
    val places : List<PlaceUI>,
    val searchQuery : String,
    val errorMessage : String?,
    val isLoading : Boolean = false
) {
companion object {
    val EMPTY =PlaceSearchState(places = emptyList(),
        searchQuery ="", errorMessage = null)

    val LOADING = PlaceSearchState(places = emptyList(),
        searchQuery ="", errorMessage = null, isLoading = true)
    }
}