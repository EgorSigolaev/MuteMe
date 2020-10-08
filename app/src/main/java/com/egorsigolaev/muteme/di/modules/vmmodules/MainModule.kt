package com.egorsigolaev.muteme.di.modules.vmmodules

import androidx.lifecycle.ViewModel
import com.egorsigolaev.muteme.di.modules.ViewModelKey
import com.egorsigolaev.muteme.presentation.screens.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel
}