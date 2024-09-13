package com.slim.placesearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slim.placesearch.data.model.response.ApiResponse
import com.slim.placesearch.data.repository.Repository
import com.slim.placesearch.data.local.SharedPrefs
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber


class MainViewModel(private val repository: Repository,
                    private val searchManager: SearchManager,
                    private val sharedPrefs : SharedPrefs) : ViewModel(){

    private val _state = MutableStateFlow(PlaceSearchState.LOADING)
    val state : StateFlow<PlaceSearchState> = _state.asStateFlow()

    private val _showError = Channel<Boolean>()
    val showError = _showError.receiveAsFlow()

    private var searchJob : Job? = null



    fun initSearchManager() {
        viewModelScope.launch {
            searchManager.init()
        }
    }

    fun onSearchQueryChanged(query : String) {
        _state.update { currentState ->
            currentState.copy(searchQuery = query, errorMessage = null, isLoading = false)
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            searchManager.searchPlaces(query).let {
                _state.update { currentState ->
                    currentState.copy(places = it, errorMessage = null, isLoading = false)
                }
            }
        }
    }

    fun getNearbyPlaces(lat: String, lon: String) {
        viewModelScope.launch {
            _state.update { PlaceSearchState.LOADING }
            if(lat.isNotEmpty() && lon.isNotEmpty() && savedLatLonChanged(lat, lon)) {
                sharedPrefs.saveLatitude(lat)
                sharedPrefs.saveLongitude(lon)
                fetchNearbyPlaces(lat,lon)
            }  else {
                getSavedPlaces()
            }
        }

    }

    suspend fun fetchNearbyPlaces(lat: String, lon: String) {
        val response = repository.getPlaces("$lat,$lon")

        response.collectLatest { result ->
            when(result) {
                is ApiResponse.Error -> {
                    Timber.e("FAILED!!!!")
                    _state.update { currentState-> currentState.copy(errorMessage = result.message?:"Error", isLoading = false) }
                    sharedPrefs.clear()
                    _showError.send(true)
                }
                is ApiResponse.Success -> {
                    result.data?.results?.let { results ->
                        val places = results.toPlacesUI()
                        searchManager.putPlaces(places)
                        _state.update { currentState ->
                            currentState.copy(places = places, errorMessage = null, isLoading = false)
                        }

                    }
                }
            }
        }

    }

    private suspend  fun getSavedPlaces() {
            searchManager.fetchAllPlaces().let {
                _state.update { currentState ->
                    currentState.copy(places = it?: emptyList(), errorMessage = null, isLoading = false)
                }
            }
    }

    fun savedLatLonChanged(lat: String, lon: String) : Boolean {
        val savedLat = sharedPrefs.getLatitude()
        val savedLon = sharedPrefs.getLongitude()
        return lat != savedLat || lon != savedLon
    }

    override fun onCleared() {
        searchManager.closeSession()
        super.onCleared()
    }
}