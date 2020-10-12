package com.egorsigolaev.muteme.presentation.extensions

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.egorsigolaev.muteme.MuteMeApp
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.presentation.screens.addplace.AddPlaceFragment
import com.egorsigolaev.muteme.presentation.screens.main.MainFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import javax.inject.Inject

fun Fragment.startLocationUpdate(fusedLocationClient: FusedLocationProviderClient, locationCallback: LocationUpdateCallback){
    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    val locationRequest = LocationRequest()
    locationRequest.fastestInterval = 2000
    locationRequest.interval = 4000
    fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let {
                for(location in it.locations){
                    val geoCoder = Geocoder(requireContext())
                    try {
                        val user = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                        val lat = user[0].latitude
                        val lng = user[0].longitude
                        Log.d(MainFragment::class.java.simpleName, "getLastKnownLocation: lat = $lat")
                        Log.d(MainFragment::class.java.simpleName, "getLastKnownLocation: lng = $lng")
                        locationCallback.onLocationUpdate(UserCoordinates(latitude = lat, longitude = lng))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } ?: run{
                Log.d(MuteMeApp.TAG, "onLocationResult: failed retrieve location")
            }

        }
    }, Looper.getMainLooper())
}

interface LocationUpdateCallback{
    fun onLocationUpdate(location: UserCoordinates)
}