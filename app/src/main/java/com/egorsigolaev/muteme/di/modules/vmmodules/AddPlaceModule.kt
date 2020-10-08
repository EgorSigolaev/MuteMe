package com.egorsigolaev.muteme.di.modules.vmmodules

import androidx.lifecycle.ViewModel
import com.egorsigolaev.muteme.di.modules.ViewModelKey
import com.egorsigolaev.muteme.presentation.screens.addplace.AddPlaceViewModel
import com.egorsigolaev.muteme.presentation.screens.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AddPlaceModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddPlaceViewModel::class)
    internal abstract fun addPlaceViewModel(viewModel: AddPlaceViewModel): ViewModel
}