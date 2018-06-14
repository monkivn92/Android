package com.vdroid.daggertwopart2

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