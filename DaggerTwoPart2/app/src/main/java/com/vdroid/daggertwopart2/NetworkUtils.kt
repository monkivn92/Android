package com.vdroid.daggertwopart2

import android.content.Context
import android.util.Log
import com.vdroid.daggertwopart2.NetChan

class NetworkUtils(context: Context, netChan: NetChan)
{
    init {
        doSomething()
    }
    fun doSomething() = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}