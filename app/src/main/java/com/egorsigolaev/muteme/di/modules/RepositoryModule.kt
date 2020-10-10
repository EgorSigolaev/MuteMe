package com.egorsigolaev.muteme.di.modules

import com.egorsigolaev.muteme.data.AddPlaceRepository
import com.egorsigolaev.muteme.data.impl.PlaceRepositoryImpl
import com.egorsigolaev.muteme.data.local.place.PlaceStorage
import com.egorsigolaev.muteme.data.PlaceRepository
import com.egorsigolaev.muteme.data.impl.AddPlaceRepositoryImpl
import com.egorsigolaev.muteme.data.source.remote.addplace.AddPlaceApi
import com.egorsigolaev.muteme.di.AppScope
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule{

    @Provides
    @AppScope
    fun providePlaceRepository(placeStorage: PlaceStorage): PlaceRepository = PlaceRepositoryImpl(placeStorage)

    @Provides
    @AppScope
    fun provideAddPlaceRepository(api: AddPlaceApi): AddPlaceRepository = AddPlaceRepositoryImpl(api)

}