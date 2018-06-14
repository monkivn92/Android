package com.vdroid.daggertwopart2

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule
{
    @Provides
    @Singleton
    fun provideRxUtils(context: Context) : RxUtils = RxUtils(context)

    @Provides
    @Singleton
    fun provideNetworkUtils(context: Context, netChan: NetChan) : NetworkUtils = NetworkUtils(context, netChan)

}