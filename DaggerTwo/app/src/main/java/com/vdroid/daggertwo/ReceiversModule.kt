package com.vdroid.daggertwo

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ReceiversModule
{
    @Provides
    @Singleton
    fun provideNetChan() : NetChan = NetChan()
}