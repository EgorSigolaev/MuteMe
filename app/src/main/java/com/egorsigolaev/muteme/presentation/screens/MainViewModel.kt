package com.egorsigolaev.muteme.presentation.screens

import android.content.pm.PackageManager
import com.egorsigolaev.muteme.presentation.base.BaseViewModel

class MainViewModel @javax.inject.Inject constructor(): BaseViewModel<MainViewState, MainViewAction, MainViewEvent>() {

    init {
        viewState = MainViewState.ScreenShowed
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
}