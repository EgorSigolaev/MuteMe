package com.egorsigolaev.muteme.presentation.screens.addplace

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.log

class AddPlaceFragment : BaseFragment(R.layout.fragment_add_place), OnMapReadyCallback,
    SearchPlaceAdapter.SearchPlaceClickListener, LocationUpdateCallback {


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


    @SuppressLint("CheckResult", "ClickableViewAccessibility")
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
        configureRecyclerView()
        RxTextView.textChanges(editTextSearchPlace)
            .map(CharSequence::toString)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ input->
                if(!input.isBlank()){
                    //recyclerViewSearchPlaces.visibility = View.VISIBLE
                    recyclerViewSearchPlaces.expand()
                    viewModel.obtainEvent(AddPlaceViewEvent.GetSearchPlaces(
                        key = getString(R.string.place_api_key),
                        input = input,
                        language = LanguageUtils.getLanguage(),
                        location = UserCoordinates(latitude = 46.425019, longitude =  30.763333)))
                }else{
                    //recyclerViewSearchPlaces.collapse()
                }
            }, {
                Log.d(TAG, "onViewCreated: ${it.printStackTrace()}")
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
                with(viewState.result.geometry.location){
                    addPlace(userCoordinates = UserCoordinates(latitude = latitude, longitude = longitude))
                }
            }
            is AddPlaceViewState.ScreenStateChanged -> {
                if(viewState.screenState is ScreenState.Loading){
                    startLoading(message = viewState.screenState.message)
                }else if(viewState.screenState is ScreenState.Finished){
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
        map.setOnMapClickListener {
            recyclerViewSearchPlaces.collapse()
        }
        requireActivity().startService(Intent(requireContext(), LocationService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
        }
    }

    private fun addPlace(userCoordinates: UserCoordinates){
        map.addMarker(MarkerOptions().position(LatLng(userCoordinates.latitude, userCoordinates.longitude)).title("Marker"))
        setCameraView(userCoordinates = userCoordinates)
    }

    private fun setCameraView(userCoordinates: UserCoordinates){
        val bottomBoundary = userCoordinates.latitude - 0.01
        val leftBoundary = userCoordinates.longitude - 0.01
        val topBoundary = userCoordinates.latitude + 0.01
        val rightBoundary = userCoordinates.longitude + 0.01
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



    override fun onSearchPlaceClick(place: SearchPlace) {
        hideKeyboard()
        recyclerViewSearchPlaces.collapse()
        editTextSearchPlace.setText("")
        viewModel.obtainEvent(AddPlaceViewEvent.GetPlaceInfo(key = getString(R.string.google_maps_api_key), placeId = place.placeId, language = LanguageUtils.getLanguage()))
    }

    override fun onLocationUpdate(location: UserCoordinates) {
        lastKnownLocation = location
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
        private const val MAPVIEW_BUNDLE_KEY_ADD_PLACE = "MAPVIEW_BUNDLE_KEY_ADD_PLACE"
    }

}