package com.vdroid.daggertwopart2

import dagger.Subcomponent

@Subcomponent(modules = [SCModule::class])
@ChatScreenScope
interface SCComponent
{
    fun inject(singleChatFragment: SingleChatFragment)
}