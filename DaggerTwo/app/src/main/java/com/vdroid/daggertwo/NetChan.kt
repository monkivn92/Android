package com.vdroid.daggertwo

import android.util.Log

class NetChan
{
    init {
        doSomething()
    }
    fun doSomething() = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}