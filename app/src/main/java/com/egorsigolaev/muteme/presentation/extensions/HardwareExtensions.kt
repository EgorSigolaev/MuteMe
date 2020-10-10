package com.egorsigolaev.muteme.presentation.extensions

import android.Manifest
import android.content.Context
import android.content.Intent
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
                MainFragment.ERROR_DIALOG_REQUEST_CODE
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
        val gpsOptionsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(gpsOptionsIntent)
        false
    } else {
        true
    }
}

fun Fragment.hasLocationPermission(): Boolean{
    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    return if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        true
    } else {
        ActivityCompat.requestPermissions(requireActivity(), permissions, MainFragment.LOCATION_PERMISSION_REQUEST_CODE)
        false
    }
}