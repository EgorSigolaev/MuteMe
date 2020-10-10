package com.egorsigolaev.muteme.presentation.screens.addplace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.egorsigolaev.muteme.MuteMeApp.Companion.TAG
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.data.models.network.SearchPlace
import com.egorsigolaev.muteme.domain.helpers.LanguageUtils
import com.egorsigolaev.muteme.presentation.base.BaseFragment
import com.egorsigolaev.muteme.presentation.extensions.gpsIsEnabled
import com.egorsigolaev.muteme.presentation.extensions.hasLocationPermission
import com.egorsigolaev.muteme.presentation.extensions.isGooglePlayServiceOK
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import com.egorsigolaev.muteme.presentation.helpers.injectViewModel
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewAction
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewEvent
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewState
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_add_place.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddPlaceFragment : BaseFragment(R.layout.fragment_add_place), OnMapReadyCallback,
    SearchPlaceAdapter.SearchPlaceClickListener {


    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var map: GoogleMap
    lateinit var viewModel: AddPlaceViewModel
    private val searchPlaceAdapter  by lazy {
        SearchPlaceAdapter(this)
    }
    private var lastKnownLocation: UserCoordinates? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        if(savedInstanceState != null){
            mapView.onCreate(savedInstanceState)
        }


    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        viewModel.apply {
            viewStates().observe(viewLifecycleOwner, Observer { bindViewState(viewState = it) })
            viewAction().observe(viewLifecycleOwner, Observer { bindViewAction(viewAction = it) })
        }
        var mapViewBundle: Bundle? = null
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY_ADD_PLACE)
        }
        mapView.onCreate(mapViewBundle)
        buttonPrevious.setOnClickListener {
            findNavController().popBackStack()
        }
        RxTextView.textChanges(editTextSearchPlace)
            .map(CharSequence::toString)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ input->
                if(!input.isBlank()){
                    recyclerViewSearchPlaces.visibility = View.VISIBLE
                    viewModel.obtainEvent(AddPlaceViewEvent.GetSearchPlaces(
                        key = getString(R.string.place_api_key),
                        input = input,
                        language = LanguageUtils.getLanguage(),
                        location = UserCoordinates(latitude = 46.425019, longitude =  30.763333)))
                }else{
                    recyclerViewSearchPlaces.visibility = View.GONE
                }
            }, {
                Log.d(TAG, "onViewCreated: ${it.printStackTrace()}")
            })
        configureRecyclerView()
    }

    private fun bindViewAction(viewAction: AddPlaceViewAction) {
        when(viewAction){
            is AddPlaceViewAction.ShowError -> showToast(viewAction.message)
        }
    }

    private fun bindViewState(viewState: AddPlaceViewState) {
        when(viewState){
            is AddPlaceViewState.SearchPlaceLoaded -> {
                searchPlaceAdapter.submitList(viewState.places)
            }
            is AddPlaceViewState.SearchPlaceError -> {
                searchPlaceAdapter.submitList(null)
            }
            is AddPlaceViewState.PlaceInfoLoaded -> {
                //TODO Add marker to map
            }
        }
    }

    private fun configureRecyclerView(){
        recyclerViewSearchPlaces.adapter = searchPlaceAdapter
        recyclerViewSearchPlaces.setHasFixedSize(true)
    }

    private fun initMap(){
        if(isGooglePlayServiceOK() && hasLocationPermission() && gpsIsEnabled()){
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
                    val userCoordinates = UserCoordinates(latitude = lat, longitude = lng)
                    lastKnownLocation = userCoordinates
                    Log.d(AddPlaceFragment::class.java.simpleName, "getLastKnownLocation: lastKnownLocation = $lastKnownLocation")
                    setCameraView(userCoordinates)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
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
        const val ERROR_DIALOG_REQUEST_CODE = 1000
        const val LOCATION_PERMISSION_REQUEST_CODE = 2000
        private const val MAPVIEW_BUNDLE_KEY_ADD_PLACE = "MAPVIEW_BUNDLE_KEY_ADD_PLACE"
    }

    override fun onSearchPlaceClick(place: SearchPlace) {
        editTextSearchPlace.setText("")
        recyclerViewSearchPlaces.visibility = View.GONE
        viewModel.obtainEvent(AddPlaceViewEvent.GetPlaceInfo(key = getString(R.string.google_maps_api_key), placeId = place.placeId, language = LanguageUtils.getLanguage()))
    }

}