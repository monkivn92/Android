package com.vdroid.daggertwo

import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(AppModule::class, UtilsModule::class, ReceiversModule::class))
@Singleton
interface AppComponent
{
    fun inject(mainActivity: MainActivity)
}