package com.egorsigolaev.muteme.presentation.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.egorsigolaev.muteme.presentation.screens.main.MainFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest


const val ERROR_DIALOG_REQUEST_CODE = 1000
const val LOCATION_PERMISSION_REQUEST_CODE = 2000
const val ENABLE_GPS_REQUEST_CODE = 3000

fun Fragment.isGooglePlayServiceOK(): Boolean{
    val available =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
    when {
        available == ConnectionResult.SUCCESS -> {
            Log.d(MainFragment::class.java.simpleName, "Google play services working")
            return true
        }
        GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                activity, available,
                ERROR_DIALOG_REQUEST_CODE
            )
            dialog.show()
        }
        else -> {
            Log.d(
                MainFragment::class.java.simpleName,
                "Google play service unavailable on this device"
            )
        }
    }
    return false
}


fun Fragment.gpsIsEnabled(): Boolean{
    val locationManager =
        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        showSetOnGPSDialog(this.activity)
        false
    } else {
        true
    }
}

fun Fragment.hasLocationPermission(): Boolean{
    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    return if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
        true
    } else {
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            LOCATION_PERMISSION_REQUEST_CODE
        )
        false
    }
}


fun showSetOnGPSDialog(activity: Activity?){
    activity?.let {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val task = LocationServices.getSettingsClient(it)
            .checkLocationSettings(builder.build())

        task.addOnSuccessListener { response ->
            val states = response.locationSettingsStates
            if (states.isLocationPresent) {
                //Do something
            }
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    // Handle result in onActivityResult()
                    e.startResolutionForResult(it, ENABLE_GPS_REQUEST_CODE)
                } catch (sendEx: IntentSender.SendIntentException) { }
            }
        }
    }
}