package com.egorsigolaev.muteme.di

import android.app.Application
import com.egorsigolaev.muteme.MuteMeApp
import com.egorsigolaev.muteme.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class,
        ScreenBindingModule::class,
        MainModule::class,
        RepositoryModule::class,
        StorageModule::class
    ]
)
@AppScope
interface AppComponent : AndroidInjector<MuteMeApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

}