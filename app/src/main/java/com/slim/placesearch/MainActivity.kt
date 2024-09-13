package com.slim.placesearch

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.slim.placesearch.ui.theme.PlaceSearchTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private lateinit var locationManager : LocationManager

    private val viewModel : MainViewModel by viewModel()
    private val locationPermissionRequest = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(this, getString(R.string.access_granted), Toast.LENGTH_SHORT).show()
                getLocation()

            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(this,
                    getString(R.string.only_approximate_location_access_granted), Toast.LENGTH_SHORT).show()
            } else -> {
            Toast.makeText(this,
                getString(R.string.permission_denied_please_enable_location_permission_to_continue), Toast.LENGTH_LONG).show()
            launchAppSettings()
        }
        }
    }

    private val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Timber.d("GPS: ${location.latitude} :: ${location.longitude}")

            viewModel.getNearbyPlaces(location.latitude.toString(),location.longitude.toString())
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Timber.d("NETWORK: ${location.latitude} :: ${location.longitude}")
            viewModel.getNearbyPlaces(location.latitude.toString(),location.longitude.toString())

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initSearchManager()
        if(!hasGrantedLocationAccess()) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        createContent()
    }

    override fun onResume() {
        super.onResume()
        if(hasGrantedLocationAccess()) {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    // permission is checked before calling this method
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(hasGps) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0F, gpsLocationListener)
        } else if(hasNetwork) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 0F, networkLocationListener)
        }


        createContent()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun createContent() {
        enableEdgeToEdge()
        setContent {
            PlaceSearchTheme {
                Scaffold (modifier = Modifier.fillMaxSize(),
                    topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name))}) }){ innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun hasGrantedLocationAccess() : Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    private fun launchAppSettings() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = Uri.parse("package:${packageName}") })
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlaceSearchTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}