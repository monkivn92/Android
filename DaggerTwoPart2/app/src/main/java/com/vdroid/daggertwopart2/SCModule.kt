package com.vdroid.daggertwopart2

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class SCModule
{
    @Provides
    @ChatScreenScope
    fun provideSCPresenter(context: Context, iChatInteract: IChatInteract) = SCPresenter(context, iChatInteract)
}