package com.egorsigolaev.muteme.presentation.screens.main

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.domain.models.MainScreenData
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import com.egorsigolaev.muteme.presentation.helpers.injectViewModel
import com.egorsigolaev.muteme.presentation.services.LocationService
import com.egorsigolaev.presentation.base.BaseFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : BaseFragment(R.layout.fragment_main), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = injectViewModel(viewModelFactory)

        viewModel.apply {
            viewStates().observe(viewLifecycleOwner, Observer { bindViewState(viewState = it) })
            viewAction().observe(viewLifecycleOwner, Observer { bindViewAction(viewAction = it) })
        }

        var mapViewBundle: Bundle? = null
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        buttonAddPlace.setOnClickListener {
            findNavController().navigate(R.id.action_to_add_place_fragment)
        }


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
                    Log.d(MainFragment::class.java.simpleName, "getLastKnownLocation: lat = $lat")
                    Log.d(MainFragment::class.java.simpleName, "getLastKnownLocation: lng = $lng")
                    map.addMarker(MarkerOptions().position(LatLng(lat, lng)).title("Marker"))
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun initMap(){
        if(isServicesOK() && hasLocationPermission() && gpsIsEnabled()){
            initGoogleMap()
        }
    }

    private fun isServicesOK(): Boolean{
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())
        when {
            available == ConnectionResult.SUCCESS -> {
                Log.d(MainFragment::class.java.simpleName, "Google play services working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), available,
                    ERROR_DIALOG_REQUEST_CODE
                )
                dialog.show()
            }
            else -> {
                Log.d(MainFragment::class.java.simpleName, "Google play service unavailable on this device")
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
                    ENABLE_GPS_REQUEST_CODE
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.obtainEvent(
            MainViewEvent.OnRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ENABLE_GPS_REQUEST_CODE -> initMap()
        }

    }

    private fun bindViewState(viewState: MainViewState){
        when(viewState){
            MainViewState.ScreenShowed -> {
            }
            is MainViewState.PlacesLoaded -> {
                val places = viewState.places
            }
        }
    }

    private fun bindViewAction(viewAction: MainViewAction){
        when(viewAction){
            is MainViewAction.ShowError -> {
                showToast(viewAction.message)
            }
            is MainViewAction.StartListenLocation -> {
                val locationServiceIntent = Intent(requireContext(), LocationService::class.java)
                locationServiceIntent.putParcelableArrayListExtra(LocationService.PLACES_EXTRA, ArrayList(viewAction.places))
                requireContext().startService(locationServiceIntent)
                requireContext().registerReceiver(userLocationBR, IntentFilter(BROADCAST_ACTION))
            }
        }
    }

    private fun initGoogleMap(){
        mapView.getMapAsync(this)
    }

    private val userLocationBR = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {locationIntent ->
                val userLocation = locationIntent.extras?.get(LocationService.SCREEN_DATA) as MainScreenData
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if(mapViewBundle == null){
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
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

    companion object{
        const val ERROR_DIALOG_REQUEST_CODE = 1000
        const val LOCATION_PERMISSION_REQUEST_CODE = 2000
        const val ENABLE_GPS_REQUEST_CODE = 3000
        const val MAPVIEW_BUNDLE_KEY = ""
        const val BROADCAST_ACTION = "BROADCAST_ACTION"
    }


}