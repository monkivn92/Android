package com.vdroid.daggertwopart2

import android.util.Log

class DataRepository : IDataRepository
{
    override fun doSomething() = Log.e("Dagger", "${this.javaClass.name} : ${hashCode()}")
}