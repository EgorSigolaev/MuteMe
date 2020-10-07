package com.egorsigolaev.muteme.presentation.screens.main

import android.content.pm.PackageManager
import com.egorsigolaev.muteme.data.source.PlaceRepository
import com.egorsigolaev.muteme.presentation.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel @javax.inject.Inject constructor(val placeRepository: PlaceRepository): BaseViewModel<MainViewState, MainViewAction, MainViewEvent>() {

    private val compositeDisposable = CompositeDisposable()

    init {
        viewState = MainViewState.ScreenShowed
        getPlaces()
    }


    private fun getPlaces(){
        val disposable = placeRepository.getAllPlaces()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe({places ->
                viewState = MainViewState.PlacesLoaded(places = places)
                if(places.isNotEmpty()){
                    viewAction = MainViewAction.StartListenLocation(places = places)
                }
            }, {
                viewAction = MainViewAction.ShowError(it.localizedMessage)
            })
        compositeDisposable.add(disposable)
    }

    override fun obtainEvent(viewEvent: MainViewEvent) {
        when(viewEvent){
            is MainViewEvent.OnRequestPermissionsResult -> {
                when(viewEvent.requestCode){
                    MainFragment.LOCATION_PERMISSION_REQUEST_CODE -> {
                        if(viewEvent.grantResults.isNotEmpty()){
                            for(result in viewEvent.grantResults){
                                if(result != PackageManager.PERMISSION_GRANTED){
                                    viewAction =
                                        MainViewAction.ShowError("Необходимо дать доступ к местоположению")
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}