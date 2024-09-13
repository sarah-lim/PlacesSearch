package com.slim.placesearch.data.local

import androidx.appsearch.annotation.Document
import androidx.appsearch.annotation.Document.Id
import androidx.appsearch.annotation.Document.LongProperty
import androidx.appsearch.annotation.Document.Namespace
import androidx.appsearch.annotation.Document.StringProperty
import androidx.appsearch.app.AppSearchSchema

@Document
data class PlaceUI(
    @Namespace
    val namespace : String,
    @Id
    val id: String,
    @StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val placeName : String,
    @StringProperty
    val placeAddress : String,
    @LongProperty
    val placeDistance: Long)