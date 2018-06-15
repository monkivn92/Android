package com.vdroid.daggertwopart2

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule
{
    @Provides
    @Singleton
    fun provideIDataRepository() : IDataRepository = DataRepository()

}