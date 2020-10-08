package com.egorsigolaev.muteme.presentation.screens.addplace

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.presentation.base.BaseFragment
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import com.egorsigolaev.muteme.presentation.helpers.injectViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_add_place.*
import javax.inject.Inject

class AddPlaceFragment : BaseFragment(R.layout.fragment_add_place), OnMapReadyCallback {


    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var map: GoogleMap
    lateinit var viewModel: AddPlaceViewModel

    private fun isServicesOK(): Boolean{
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())
        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d(AddPlaceFragment::class.java.simpleName, "Google play services working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), available,
                    AddPlaceFragment.ERROR_DIALOG_REQUEST_CODE
                )
                dialog.show()
            }
            else -> {
                Log.d(AddPlaceFragment::class.java.simpleName, "Google play service unavailable on this device")
            }
        }
        return false
    }

    private fun hasLocationPermission(): Boolean{
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        return if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            true
        }else{
            ActivityCompat.requestPermissions(requireActivity(), permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        if(savedInstanceState != null){
            mapView.onCreate(savedInstanceState)
        }
        Places.initialize(requireContext(), "AIzaSyClQ_mQPHWE-K9hDoR8TPv6hk40K1Dqsu4")


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        var mapViewBundle: Bundle? = null
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(AddPlaceFragment.MAPVIEW_BUNDLE_KEY_ADD_PLACE)
        }
        mapView.onCreate(mapViewBundle)
        buttonPrevious.setOnClickListener {
            findNavController().popBackStack()
        }
        editTextSearchPlace.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                val fieldList = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                    .build(requireContext())
                startActivityForResult(intent, AUTOCOMPLETE_INTENT_CODE)
            }
        }
    }

    private fun gpsIsEnabled(): Boolean{
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledDialog()
            false
        }else{
            true
        }
    }

    private fun showGPSDisabledDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("GPS Disabled")
        builder.setMessage("Gps is disabled, in order to use the application properly you need to enable GPS of your device")
        builder.setPositiveButton("Enable GPS", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    AddPlaceFragment.ENABLE_GPS_REQUEST_CODE
                )
            }
        }).setNegativeButton("No, Just Exit", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                requireActivity().finish()
            }
        })
        val mGPSDialog = builder.create()
        mGPSDialog.show()
    }

    private fun initMap(){
        if(isServicesOK() && hasLocationPermission() && gpsIsEnabled()){
            initGoogleMap()
        }
    }

    private fun initGoogleMap(){
        mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLastKnownLocation()
    }

    private fun getLastKnownLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnCompleteListener {task ->
            if(task.isSuccessful){
                val location = task.result
                val geoCoder = Geocoder(requireContext())
                if(location == null){
                    return@addOnCompleteListener
                }
                try {
                    val user = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    val lat = user[0].latitude
                    val lng = user[0].longitude
                    Log.d(AddPlaceFragment::class.java.simpleName, "getLastKnownLocation: lat = $lat")
                    Log.d(AddPlaceFragment::class.java.simpleName, "getLastKnownLocation: lng = $lng")

                    setCameraView(UserCoordinates(latitude = lat, longitude = lng))
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            AUTOCOMPLETE_INTENT_CODE -> {
                data?.let {
                    if (resultCode == Activity.RESULT_OK) {
                        val place = Autocomplete.getPlaceFromIntent(it)
                        editTextSearchPlace.setText(place.address)
                        place.latLng?.let {latlng ->
                            with(latlng){
                                putMarker(UserCoordinates(latitude = latitude, longitude = longitude))
                            }
                        }
                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        val status = Autocomplete.getStatusFromIntent(it)
                        showToast(status.statusMessage!!)
                    }else{

                    }
                }

            }
        }
    }

    private fun putMarker(userCoordinates: UserCoordinates){
        map.addMarker(MarkerOptions().position(LatLng(userCoordinates.latitude, userCoordinates.longitude)).title("Marker"))
        setCameraView(userCoordinates = userCoordinates)
    }

    private fun setCameraView(userCoordinates: UserCoordinates){
        val bottomBoundary = userCoordinates.latitude - .05
        val leftBoundary = userCoordinates.longitude - .05
        val topBoundary = userCoordinates.latitude + .05
        val rightBoundary = userCoordinates.longitude + .05
        val mapBoundary = LatLngBounds(
            LatLng(bottomBoundary, leftBoundary),
            LatLng(topBoundary, rightBoundary)
        )
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary, 0))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY_ADD_PLACE)
        if(mapViewBundle == null){
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY_ADD_PLACE, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        initMap()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    companion object{
        const val AUTOCOMPLETE_INTENT_CODE = 4000
        const val ERROR_DIALOG_REQUEST_CODE = 1000
        const val LOCATION_PERMISSION_REQUEST_CODE = 2000
        const val ENABLE_GPS_REQUEST_CODE = 3000
        private const val MAPVIEW_BUNDLE_KEY_ADD_PLACE = "MAPVIEW_BUNDLE_KEY_ADD_PLACE"
    }

}