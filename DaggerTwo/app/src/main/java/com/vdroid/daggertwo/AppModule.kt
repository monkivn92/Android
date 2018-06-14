package com.vdroid.daggertwo

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(context: Context)
{
    private var appContext : Context = context

    @Provides
    @Singleton
    fun provideContext() : Context = appContext
}