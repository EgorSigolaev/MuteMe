package com.egorsigolaev.muteme.di.modules

import android.app.Application
import com.egorsigolaev.muteme.di.AppScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
class LocationModule{

    @Provides
    @AppScope
    fun provideFusedLocationProvider(application: Application): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application.applicationContext)

}