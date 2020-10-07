package com.egorsigolaev.muteme.di.modules

import com.egorsigolaev.muteme.data.impl.PlaceRepositoryImpl
import com.egorsigolaev.muteme.data.local.place.PlaceStorage
import com.egorsigolaev.muteme.data.source.PlaceRepository
import com.egorsigolaev.muteme.di.AppScope
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule{

    @Provides
    @AppScope
    fun providePlaceRepository(placeStorage: PlaceStorage): PlaceRepository = PlaceRepositoryImpl(placeStorage)

}