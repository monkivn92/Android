package com.vdroid.daggertwopart2

import dagger.Subcomponent

@ChatScope
@Subcomponent(modules = [ChatModule::class])
interface ChatComponent
{
    fun plusSCComponent(scModule : SCModule) : SCComponent
    fun inject(mainActivity: MainActivity)
}