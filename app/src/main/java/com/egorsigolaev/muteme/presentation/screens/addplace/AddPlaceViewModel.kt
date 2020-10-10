package com.egorsigolaev.muteme.presentation.screens.addplace

import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.constants.network.GoogleApiResponseCodes
import com.egorsigolaev.muteme.constants.network.GoogleApiResponseCodes.OK
import com.egorsigolaev.muteme.constants.network.GoogleApiResponseCodes.UNKNOWN_ERROR
import com.egorsigolaev.muteme.constants.network.GoogleApiResponseCodes.ZERO_RESULTS
import com.egorsigolaev.muteme.data.AddPlaceRepository
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.presentation.base.BaseViewModel
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewAction
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewEvent
import com.egorsigolaev.muteme.presentation.screens.addplace.models.AddPlaceViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddPlaceViewModel @Inject constructor(private val repository: AddPlaceRepository): BaseViewModel<AddPlaceViewState, AddPlaceViewAction, AddPlaceViewEvent>() {

    private val searchPlacesCompositeDisposable = CompositeDisposable()
    private val placeInfoCompositeDisposable = CompositeDisposable()

    private fun getSearchPlaces(key: String, input: String, language: String, location: UserCoordinates?){
        searchPlacesCompositeDisposable.clear()
        val disposable = repository.getSearchPlaces(key = key, input = input, language = language, location = location)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when(it.status){
                    OK -> {
                        viewState = AddPlaceViewState.SearchPlaceLoaded(places = it.predictions)
                    }
                    UNKNOWN_ERROR -> {
                        viewState = AddPlaceViewState.SearchPlaceError(errorCode = UNKNOWN_ERROR)
                        viewAction = AddPlaceViewAction.ShowError(R.string.place_search_unknown_error)
                    }
                    ZERO_RESULTS -> {
                        viewState = AddPlaceViewState.SearchPlaceError(errorCode = ZERO_RESULTS)
                        viewAction = AddPlaceViewAction.ShowError(R.string.place_search_zero_results_error)
                    }
                }

            }, {
                viewAction = AddPlaceViewAction.ShowError(it.localizedMessage)
            })
        searchPlacesCompositeDisposable.add(disposable)
    }

    private fun getPlaceInfo(key: String, placeId: String, language: String){
        placeInfoCompositeDisposable.clear()
        val disposable = repository.getPlaceInfo(key = key, placeId = placeId, language = language)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when(it.status){
                    OK -> {
                        viewState = AddPlaceViewState.PlaceInfoLoaded(result = it.result)
                    }
                    UNKNOWN_ERROR -> {
                        viewState = AddPlaceViewState.SearchPlaceError(errorCode = UNKNOWN_ERROR)
                        viewAction = AddPlaceViewAction.ShowError(R.string.place_search_unknown_error)
                    }
                    ZERO_RESULTS -> {
                        viewState = AddPlaceViewState.SearchPlaceError(errorCode = ZERO_RESULTS)
                        viewAction = AddPlaceViewAction.ShowError(R.string.place_search_zero_results_error)
                    }
                }
            }, {
                viewAction = AddPlaceViewAction.ShowError(it.localizedMessage)
            })
        placeInfoCompositeDisposable.add(disposable)
    }


    override fun obtainEvent(viewEvent: AddPlaceViewEvent) {
        when(viewEvent){
            is AddPlaceViewEvent.GetSearchPlaces -> {
                with(viewEvent){
                    getSearchPlaces(key = key, input = input, language = language, location = location)
                }
            }
            is AddPlaceViewEvent.GetPlaceInfo -> {
                with(viewEvent){
                    getPlaceInfo(key = key, placeId = placeId, language = language)
                }
            }
        }
    }

    override fun onCleared() {
        searchPlacesCompositeDisposable.dispose()
        placeInfoCompositeDisposable.dispose()
        super.onCleared()
    }

}