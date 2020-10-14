package com.egorsigolaev.muteme.presentation.screens.addplace

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.egorsigolaev.muteme.MuteMeApp.Companion.TAG
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.data.models.network.SearchPlace
import com.egorsigolaev.muteme.domain.helpers.LanguageUtils
import com.egorsigolaev.muteme.presentation.base.BaseFragment
import com.egorsigolaev.muteme.presentation.extensions.*
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import com.egorsigolaev.muteme.presentation.helpers.injectViewModel
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewAction
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewEvent
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewState
import com.egorsigolaev.muteme.presentation.screens.addplace.models.ScreenState
import com.egorsigolaev.muteme.presentation.services.LocationService
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jakewharton.rxbinding2.widget.RxTextView
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_add_place.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddPlaceFragment : BaseFragment(R.layout.fragment_add_place), OnMapReadyCallback,
    SearchPlaceAdapter.SearchPlaceClickListener {


    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient
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
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        viewModel.apply {
            viewStates().observe(viewLifecycleOwner, Observer { bindViewState(viewState = it) })
            viewAction().observe(viewLifecycleOwner, Observer { bindViewAction(viewAction = it) })
        }
        mapView.onCreate(savedInstanceState)
        buttonPrevious.setOnClickListener {
            findNavController().popBackStack()
        }
        configureRecyclerView()
        configureSearchPlaceEditText()
        initMap()
    }

    @SuppressLint("CheckResult")
    private fun configureSearchPlaceEditText(){
        RxTextView.textChanges(editTextSearchPlace)
            .map(CharSequence::toString)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ input ->
                if (!input.isBlank()) {
                    recyclerViewSearchPlaces.expand()
                    viewModel.obtainEvent(
                        AddPlaceViewEvent.GetSearchPlaces(
                            key = getString(R.string.place_api_key),
                            input = input,
                            language = LanguageUtils.getLanguage(),
                            location = UserCoordinates(latitude = 46.425019, longitude = 30.763333)
                        )
                    )
                } else {
                    recyclerViewSearchPlaces.collapse()
                }
            }, {
                showToast(it.localizedMessage)
            })
    }

    private fun bindViewAction(viewAction: AddPlaceViewAction) {
        when(viewAction){
            is AddPlaceViewAction.ShowError -> showToast(viewAction.message)
            is AddPlaceViewAction.CollapseRecyclerView -> recyclerViewSearchPlaces.collapse()
        }
    }

    private fun bindViewState(viewState: AddPlaceViewState) {
        when(viewState){
            is AddPlaceViewState.SearchPlaceLoaded -> {
                submitPlaces(places = viewState.places)
            }
            is AddPlaceViewState.SearchPlaceError -> {
                submitPlaces(places = emptyList())
            }
            is AddPlaceViewState.PlaceInfoLoaded -> {
                with(viewState.result.geometry.location) {
                    setPlace(
                        userCoordinates = UserCoordinates(
                            latitude = latitude,
                            longitude = longitude
                        )
                    )
                }
            }
            is AddPlaceViewState.ScreenStateChanged -> {
                if (viewState.screenState is ScreenState.Loading) {
                    startLoading(message = viewState.screenState.message)
                } else if (viewState.screenState is ScreenState.Finished) {
                    stopLoading()
                }
            }
            else -> throw RuntimeException("not all view states checked")
        }
    }

    private fun submitPlaces(places: List<SearchPlace>){
        searchPlaceAdapter.submitList(places)
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
        getLastKnownLocation(fusedLocationClient, object : LocationUpdateCallback{
            override fun onLocationUpdate(location: UserCoordinates) { setPlace(location) }
        })
        map.projection.visibleRegion.latLngBounds.center.apply {
            setPlace(UserCoordinates(latitude = this.latitude, longitude = this.longitude))
        }
        map.setOnMapClickListener {
            if(recyclerViewSearchPlaces.visibility == View.VISIBLE){
                recyclerViewSearchPlaces.collapse()
            }
        }
        map.setOnCameraMoveListener {
            val cameraPosition: CameraPosition = googleMap.cameraPosition
        }
        map.setOnCameraIdleListener {
            val center = map.projection.visibleRegion.latLngBounds.center
            val moveDownAnimation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.map_marker_move_down
            )
            moveDownAnimation.fillAfter = true
            mapMarker.startAnimation(moveDownAnimation)
            //Log.d(TAG, "onMapReady: marker has been put, latitude = " + center.latitude + " longitude = " + center.longitude)
        }
        map.setOnCameraMoveStartedListener {
            val moveUpAnimation = AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.map_marker_move_up
            )
            moveUpAnimation.fillAfter = true
            mapMarker.startAnimation(moveUpAnimation)
            //Log.d(TAG, "onMapReady: setOnCameraMoveStartedListener")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_GPS_ENABLE -> {
                Log.d(TAG, "onActivityResult: ")
            }
        }
    }

    private fun setPlace(userCoordinates: UserCoordinates){
        //setMarker(LatLng(userCoordinates.latitude, userCoordinates.longitude))
        setCameraView(userCoordinates = userCoordinates)
    }

    private fun setMarker(latLng: LatLng){
        map.clear()
        map.addMarker(MarkerOptions().position(latLng).title("Marker"))
    }

    private fun setCameraView(userCoordinates: UserCoordinates){
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    userCoordinates.latitude,
                    userCoordinates.longitude
                ), 18f
            )
        )
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



    override fun onSearchPlaceClick(place: SearchPlace) {
        hideKeyboard()
        recyclerViewSearchPlaces.collapse(finishAnimFunc = { searchPlaceAdapter.submitList(emptyList()) })
        editTextSearchPlace.clear()
        viewModel.obtainEvent(
            AddPlaceViewEvent.GetPlaceInfo(
                key = getString(R.string.google_maps_api_key),
                placeId = place.placeId,
                language = LanguageUtils.getLanguage()
            )
        )
    }

    private fun registerLocationListener(){
        requireContext().startService(Intent(requireContext(), LocationService::class.java))
        val intentFilter = IntentFilter().apply {
            addAction(LocationService.LOCATION_ACTION)
            addAction(LocationService.GPS_OFF_ERROR_ACTION)
        }
        requireContext().registerReceiver(locationBR, intentFilter)
    }

    private fun unregisterLocationListener(){
        requireContext().stopService(Intent(requireContext(), LocationService::class.java))
        requireContext().unregisterReceiver(locationBR)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        registerLocationListener()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        unregisterLocationListener()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private val locationBR = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { locationIntent ->
                lastKnownLocation = locationIntent.extras?.get(LocationService.LOCATION_DATA) as? UserCoordinates
                (locationIntent.extras?.get(LocationService.GPS_OFF_ERROR_ACTION) as? ResolvableApiException)?.let {
                    it.startResolutionForResult(
                        requireActivity(),
                        REQUEST_GPS_ENABLE
                    )
                }
            }
        }

    }

    companion object{
        private const val REQUEST_GPS_ENABLE = 1234
        private const val MAPVIEW_BUNDLE_KEY_ADD_PLACE = "MAPVIEW_BUNDLE_KEY_ADD_PLACE"
    }

}