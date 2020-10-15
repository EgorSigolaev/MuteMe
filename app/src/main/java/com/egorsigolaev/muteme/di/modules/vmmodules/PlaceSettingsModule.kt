package com.egorsigolaev.muteme.di.modules.vmmodules

import androidx.lifecycle.ViewModel
import com.egorsigolaev.muteme.di.modules.ViewModelKey
import com.egorsigolaev.muteme.presentation.screens.main.MainViewModel
import com.egorsigolaev.muteme.presentation.screens.placesettings.PlaceSettingsViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PlaceSettingsModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlaceSettingsViewModel::class)
    internal abstract fun placeSettingsViewModel(viewModel: PlaceSettingsViewModel): ViewModel

}