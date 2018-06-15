package com.vdroid.daggertwopart2

import android.content.Context
import android.util.Log

class SCPresenter(context: Context, iChatInteract: IChatInteract) : ISCPresenter
{
    override fun doSomething(): Int = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}

