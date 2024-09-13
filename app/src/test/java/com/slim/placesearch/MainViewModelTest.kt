package com.slim.placesearch

import com.google.gson.Gson
import com.slim.placesearch.data.local.PlaceUI
import com.slim.placesearch.data.local.SharedPrefs
import com.slim.placesearch.data.model.response.ApiResponse
import com.slim.placesearch.data.model.response.PlacesResponse
import com.slim.placesearch.data.repository.Repository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.io.InputStreamReader

class MainViewModelTest {

    private val repository = mockk<Repository>()
    private val searchManager = mockk<SearchManager>()
    private val sharedPrefs = mockk<SharedPrefs>()

    @Before
    fun setup() {

    }

    @Test
    fun getState() {

    }

    @Test
    fun getShowError() {

    }

    @Test
    fun onSearchQueryChanged() {
    }

    @Test
    fun `get_nearby_places_from_saved_success`() = runTest{
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {
            coEvery { searchManager.fetchAllPlaces() } returns savedPlaces()
            val viewModel = MainViewModel(repository, searchManager, sharedPrefs)

            viewModel.getNearbyPlaces("", "")
            assertEquals(viewModel.state.value.places, savedPlaces())
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `get_nearby_places_from_api_success`() = runTest{
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {

            val viewModel = MainViewModel(repository, searchManager, sharedPrefs)
            coEvery { repository.getPlaces(any())} returns flowOf(ApiResponse.Success(data = getPlacesResponse()))
            every { sharedPrefs.getLatitude() }returns "37.421998333333335"
            every { sharedPrefs.getLongitude() } returns "-122.084"
            every { sharedPrefs.saveLongitude(any()) } just runs
            every { sharedPrefs.saveLatitude(any()) } just runs
            coEvery { searchManager.putPlaces(any()) } returns true

            viewModel.getNearbyPlaces("37.5555", "-122.084")
            assertNotNull(viewModel.state.value.places)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `get_nearby_places_from_api_fail`() = runTest{
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {

            val viewModel = MainViewModel(repository, searchManager, sharedPrefs)
            coEvery { repository.getPlaces(any())} returns flowOf(ApiResponse.Error(data = null, message = "Error"))
            every { sharedPrefs.getLatitude() }returns "37.421998333333335"
            every { sharedPrefs.getLongitude() } returns "-122.084"
            every { sharedPrefs.saveLongitude(any()) } just runs
            every { sharedPrefs.saveLatitude(any()) } just runs
            every { sharedPrefs.clear() } just runs

            coEvery { searchManager.putPlaces(any()) } returns true
            viewModel.getNearbyPlaces("37.5555", "-122.084")
            assertTrue(viewModel.state.value.places.isEmpty())
            assertEquals("Error", viewModel.state.value.errorMessage)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `fetch_nearby_places_success`() = runTest{
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try {

            val viewModel = MainViewModel(repository, searchManager, sharedPrefs)
            coEvery { repository.getPlaces(any())} returns flowOf(ApiResponse.Success(data = getPlacesResponse()))
            every { sharedPrefs.getLatitude() }returns "37.421998333333335"
            every { sharedPrefs.getLongitude() } returns "-122.084"
            coEvery { searchManager.putPlaces(any()) } returns true

            viewModel.fetchNearbyPlaces("37.5555", "-122.084")
            assertEquals(savedPlaces(),viewModel.state.value.places)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `check_saved_lat_long_changed`() {
        val viewModel = MainViewModel(repository, searchManager, sharedPrefs)
        every { sharedPrefs.getLatitude() }returns "37.421998333333335"
        every { sharedPrefs.getLongitude() } returns "-122.084"

        assertFalse(viewModel.savedLatLonChanged("37.421998333333335", "-122.084"))
    }


    private fun savedPlaces() : List<PlaceUI> {
        val inputStream = javaClass.classLoader?.getResourceAsStream("savedPlaces.json")
        return Gson().fromJson(InputStreamReader(inputStream), Array<PlaceUI>::class.java).asList()
    }

    private fun getPlacesResponse() : PlacesResponse {
        val inputStream = javaClass.classLoader?.getResourceAsStream("placeResponse.json")
        return Gson().fromJson(InputStreamReader(inputStream), PlacesResponse::class.java)
    }
}