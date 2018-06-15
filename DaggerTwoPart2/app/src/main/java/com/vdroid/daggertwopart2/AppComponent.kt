package com.vdroid.daggertwopart2

import android.content.Context
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, UtilsModule::class, ReceiversModule::class, DataModule::class])
@Singleton
interface AppComponent
{
    fun plusChatComponent(chatModule : ChatModule) : ChatComponent
    //fun inject(mainActivity: MainActivity) -- commented out to fix conflict with inject(mainActivity: MainActivity) in ChatComponent
}