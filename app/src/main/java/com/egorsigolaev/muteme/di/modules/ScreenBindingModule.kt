package com.egorsigolaev.muteme.di.modules

import com.egorsigolaev.muteme.presentation.screens.addplace.AddPlaceFragment
import com.egorsigolaev.muteme.presentation.screens.main.MainFragment
import com.egorsigolaev.muteme.presentation.screens.placesettings.PlaceSettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScreenBindingModule {

    @ContributesAndroidInjector
    abstract fun mainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun addPlaceFragment(): AddPlaceFragment

    @ContributesAndroidInjector
    abstract fun placeSettingsFragment(): PlaceSettingsFragment
}