package com.slim.placesearch.data.repository

import com.google.gson.Gson
import com.slim.placesearch.data.model.response.PlacesResponse
import com.slim.placesearch.data.repository.api.PlacesApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Test
import org.mockito.kotlin.any
import java.io.IOException
import java.io.InputStreamReader


class RepositoryImplTest {

    private val placeApiMock = mockk<PlacesApi>()

    @Test
    fun `success_get_places`(): Unit = runBlocking{
        coEvery { placeApiMock.getPlaces(any()) }returns  getPlacesResponse()
        val repository = RepositoryImpl(placeApiMock)
        repository.getPlaces(any()).collectLatest { result ->
                assertNotNull(result)
            }
    }

    @Test
    fun `failure_get_place`(): Unit = runBlocking {
        val message = "Error getting places"
        coEvery { placeApiMock.getPlaces(any()) } throws IOException()
        val repository = RepositoryImpl(placeApiMock)
        repository.getPlaces(any()).collectLatest { result ->
            assertEquals(message, result.message)
        }
    }


    private fun getPlacesResponse() : PlacesResponse {
        val inputStream = javaClass.classLoader?.getResourceAsStream("placeResponse.json")
        return Gson().fromJson(InputStreamReader(inputStream), PlacesResponse::class.java)
    }
}