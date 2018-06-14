package com.vdroid.daggertwopart2

import android.util.Log

class NetChan
{
    init {
        doSomething()
    }
    fun doSomething() = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}