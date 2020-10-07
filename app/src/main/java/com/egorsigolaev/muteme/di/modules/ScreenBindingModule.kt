package com.egorsigolaev.muteme.di.modules

import com.egorsigolaev.muteme.presentation.screens.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScreenBindingModule {

    @ContributesAndroidInjector
    abstract fun mainFragment(): MainFragment
}