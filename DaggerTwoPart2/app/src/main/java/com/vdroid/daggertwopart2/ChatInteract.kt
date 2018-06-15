package com.vdroid.daggertwopart2

import android.content.Context
import android.util.Log

class ChatInteract(context: Context, iDataRepository : IDataRepository, iChatStateController : IChatStateController): IChatInteract
{
    override fun doSomething() : Int = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}