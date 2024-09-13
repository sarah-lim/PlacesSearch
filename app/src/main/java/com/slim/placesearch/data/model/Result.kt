package com.slim.placesearch.data.model


data class Result(

    val categories: List<Category>,
    val chains: List<Any>,
    val closed_bucket: String,
    var distance: Int,
    val fsq_id: String,
    val geocodes: Geocodes,
    val link: String,
    var location: Location,
    var name: String,
    val related_places: RelatedPlaces,
    val timezone: String
)