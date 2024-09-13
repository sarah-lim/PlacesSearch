package com.slim.placesearch

import com.slim.placesearch.data.local.PlaceUI
import com.slim.placesearch.data.model.Result


fun List<Result>.toPlacesUI() : List<PlaceUI> {
        return this.map { PlaceUI("my_places",
            id = it.fsq_id,
            placeName = it.name,
            placeAddress = it.location.formatted_address,
            placeDistance = it.distance.toLong()) }
}