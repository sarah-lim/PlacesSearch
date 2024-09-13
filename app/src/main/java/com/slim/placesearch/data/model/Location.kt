package com.slim.placesearch.data.model


data class Location(
    var formatted_address: String,
    val address: String,
    val census_block: String,
    val country: String,
    val cross_street: String,
    val dma: String,
    val locality: String,
    val postcode: String,
    val region: String
)