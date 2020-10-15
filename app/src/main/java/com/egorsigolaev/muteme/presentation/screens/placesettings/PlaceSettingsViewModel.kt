package com.egorsigolaev.muteme.presentation.screens.placesettings

import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.data.PlaceRepository
import com.egorsigolaev.muteme.data.models.Place
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.domain.models.VolumeMode
import com.egorsigolaev.muteme.presentation.base.BaseViewModel
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsLoadingState
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewAction
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewEvent
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.RuntimeException
import javax.inject.Inject

class PlaceSettingsViewModel @Inject constructor(private val placeRepository: PlaceRepository) : BaseViewModel<PlaceSettingsViewState,PlaceSettingsViewAction,PlaceSettingsViewEvent>() {

    private val placeCompositeDisposable = CompositeDisposable()

    init {
        viewState = PlaceSettingsViewState(volumeMode = VolumeMode.NONE, loadingState = PlaceSettingsLoadingState.VolumeModeNotSelected)
    }

    override fun obtainEvent(viewEvent: PlaceSettingsViewEvent) {
        when(viewEvent){
            is PlaceSettingsViewEvent.SelectVolumeMode -> {
                viewState = viewState.copy(volumeMode = viewEvent.volumeMode, loadingState = PlaceSettingsLoadingState.VolumeModeSelected)
            }
            is PlaceSettingsViewEvent.SavePlace -> {
                addPlace(place = viewEvent.place)
            }
        }
    }

    private fun addPlace(place: Place){
        placeCompositeDisposable.clear()
        viewState = viewState.copy(loadingState = PlaceSettingsLoadingState.PlaceSavingStarted)
        val disposable = placeRepository.addPlace(place = place)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState = viewState.copy(loadingState = PlaceSettingsLoadingState.PlaceSavingFinished)
            }, {
                viewAction = PlaceSettingsViewAction.ShowError(message = it.localizedMessage)
            })
        placeCompositeDisposable.add(disposable)
    }

    fun getPlace(name: String, description: String, volumeMode: VolumeMode, placeCoordinates: UserCoordinates, enterRadiusMeters: Int): Place{
        var placeDescription: String? = null
        if(description.isNotEmpty()){
            placeDescription = description
        }
        return Place(
            name = name,
            description = placeDescription,
            coordinates = placeCoordinates,
            needVolumeMode = volumeMode,
            enterRadiusMeters = enterRadiusMeters
        )
    }

    fun getSelectedVolumeMode(): VolumeMode{
        viewState.volumeMode?.let {
            return it
        } ?: run{
            throw RuntimeException("PlaceSettingsViewModel: volume mode is null")
        }
    }

    fun fieldsFilledCorrect(placeName: String, placeDescription: String): Boolean{
        if(placeName.isEmpty()){
            viewAction = PlaceSettingsViewAction.ShowError(R.string.place_name_not_filled_error)
            return false
        }
        return true
    }

    override fun onCleared() {
        placeCompositeDisposable.dispose()
        super.onCleared()
    }

}