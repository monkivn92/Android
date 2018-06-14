package com.vdroid.daggertwo

import android.content.Context
import android.util.Log
import com.vdroid.daggertwo.NetChan

class NetworkUtils(context: Context, netChan: NetChan)
{
    init {
        doSomething()
    }
    fun doSomething() = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}