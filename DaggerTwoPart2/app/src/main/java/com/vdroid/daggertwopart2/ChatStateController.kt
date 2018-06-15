package com.vdroid.daggertwopart2

import android.util.Log

class ChatStateController(rxUtils: RxUtils) : IChatStateController
{
    override fun doSomething(): Int = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}