package com.slim.placesearch

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.slim.placesearch.data.local.PlaceUI
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val viewModel : MainViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = viewModel.showError) {
        viewModel.showError.collectLatest { show ->
            if(show) {
                Toast.makeText(context,state.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

    }

    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center) {
        TextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
                .fillMaxWidth()
        )
        if(state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        } else {
            if(state.places.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = "No nearby place found")
                }
            } else {

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.places.size) { index ->
                        PlaceItem(state.places[index])
                        Divider()
                    }
                }
            }
        }

    }

}

@Composable
fun PlaceItem(place : PlaceUI) {
    val name = place.placeName
    val address = place.placeAddress
    val distance = place.placeDistance

    ConstraintLayout(modifier = Modifier.padding(all = dimensionResource(id = R.dimen.padding_small)) ) {
        val (placeName, placeAddress, placeDistance) = createRefs()
        Text(modifier = Modifier
            .constrainAs(placeName){
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            text = name)
        Text(modifier = Modifier
            .constrainAs(placeAddress){
                top.linkTo(placeName.bottom)
                start.linkTo(parent.start)
            },
            text = address)
        Text(modifier = Modifier
            .constrainAs(placeDistance){
                top.linkTo(placeAddress.bottom)
                start.linkTo(parent.start)
            },
            text = stringResource(R.string.distance, distance)
        )
    }

}

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = DividerDefaults.color,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.thickness_divider))
            .background(color = color)
    )
}
