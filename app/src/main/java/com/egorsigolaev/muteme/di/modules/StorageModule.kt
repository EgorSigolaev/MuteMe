package com.egorsigolaev.muteme.di.modules

import android.app.Application
import androidx.room.Room
import com.egorsigolaev.muteme.data.local.MuteMeDatabase
import com.egorsigolaev.muteme.data.local.place.PlaceStorage
import com.egorsigolaev.muteme.data.local.place.PlaceStorageImpl
import com.egorsigolaev.muteme.di.AppScope
import dagger.Module
import dagger.Provides

@Module
class StorageModule {

    @Provides
    @AppScope
    fun provideDatabase(application: Application) = Room.databaseBuilder(
        application.applicationContext,
        MuteMeDatabase::class.java,
        "mute_me_db"
    ).build()


    @Provides
    @AppScope
    fun providePlaceStorage(muteMeDatabase: MuteMeDatabase): PlaceStorage =
        PlaceStorageImpl(muteMeDatabase.placeDao())


}