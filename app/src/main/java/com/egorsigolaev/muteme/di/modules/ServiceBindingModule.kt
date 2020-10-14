package com.egorsigolaev.muteme.di.modules

import com.egorsigolaev.muteme.presentation.screens.main.MainFragment
import com.egorsigolaev.muteme.presentation.services.LocationService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBindingModule {

    @ContributesAndroidInjector
    abstract fun locationService(): LocationService

}