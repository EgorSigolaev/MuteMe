package com.egorsigolaev.muteme.presentation.screens.main

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.egorsigolaev.muteme.MuteMeApp.Companion.TAG
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.domain.models.MainScreenData
import com.egorsigolaev.muteme.presentation.MainActivity
import com.egorsigolaev.muteme.presentation.base.BaseFragment
import com.egorsigolaev.muteme.presentation.extensions.ENABLE_GPS_REQUEST_CODE
import com.egorsigolaev.muteme.presentation.extensions.gpsIsEnabled
import com.egorsigolaev.muteme.presentation.extensions.hasLocationPermission
import com.egorsigolaev.muteme.presentation.extensions.isGooglePlayServiceOK
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import com.egorsigolaev.muteme.presentation.helpers.injectViewModel
import com.egorsigolaev.muteme.presentation.screens.main.models.MainViewAction
import com.egorsigolaev.muteme.presentation.screens.main.models.MainViewEvent
import com.egorsigolaev.muteme.presentation.screens.main.models.MainViewState
import com.egorsigolaev.muteme.presentation.services.LocationService
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_add_place.*
import kotlinx.android.synthetic.main.fragment_add_place.mapView
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : BaseFragment(R.layout.fragment_main), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var map: GoogleMap


    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = injectViewModel(viewModelFactory)

        viewModel.apply {
            viewStates().observe(viewLifecycleOwner, Observer { bindViewState(viewState = it) })
            viewAction().observe(viewLifecycleOwner, Observer { bindViewAction(viewAction = it) })
        }
        mapView.onCreate(savedInstanceState)
        buttonMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        buttonAddPlace.setOnClickListener {
            //drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.action_to_add_place_fragment)
        }

    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }

    private fun initMap() {
        if (isGooglePlayServiceOK() && hasLocationPermission() && gpsIsEnabled()) {
            initGoogleMap()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.obtainEvent(MainViewEvent.OnRequestPermissionsResult(requestCode, permissions, grantResults))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_GPS_REQUEST_CODE -> initMap()
        }

    }

    private fun bindViewState(viewState: MainViewState) {
        when (viewState) {
            MainViewState.ScreenShowed -> {
            }
            is MainViewState.PlacesLoaded -> {
                val places = viewState.places
            }
        }
    }

    private fun bindViewAction(viewAction: MainViewAction) {
        when (viewAction) {
            is MainViewAction.ShowError -> {
                showToast(viewAction.message)
            }
            is MainViewAction.StartListenLocation -> {
                val locationServiceIntent = Intent(requireContext(), LocationService::class.java)
                locationServiceIntent.putParcelableArrayListExtra(
                    LocationService.PLACES_EXTRA,
                    ArrayList(viewAction.places)
                )
                requireContext().startService(locationServiceIntent)
                requireContext().registerReceiver(userLocationBR, IntentFilter(LocationService.LOCATION_ACTION))
            }
        }
    }

    private fun initGoogleMap() {
        mapView.getMapAsync(this)
    }

    private val userLocationBR = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { locationIntent ->
                val userLocation = locationIntent.extras?.get(LocationService.SCREEN_DATA) as MainScreenData
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLastKnownLocation()
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

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}