package com.egorsigolaev.muteme.di.modules

import androidx.lifecycle.ViewModelProvider
import com.egorsigolaev.muteme.presentation.helpers.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(
        factory: ViewModelFactory
    ): ViewModelProvider.Factory
}