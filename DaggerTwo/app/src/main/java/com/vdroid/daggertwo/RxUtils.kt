package com.vdroid.daggertwo

import android.content.Context
import android.util.Log

class RxUtils(context : Context)
{
    init {
        doSomething()
    }
    fun doSomething() = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}