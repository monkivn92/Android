package com.vdroid.daggertwopart2

import android.app.Application

class DTApp : Application()
{
    companion object {
        private lateinit var appCom : AppComponent
        fun getComponent() : AppComponent = appCom
    }

    override fun onCreate() {
        super.onCreate()
        appCom = buidComponent()
    }

    protected fun buidComponent() : AppComponent = DaggerAppComponent.builder()
                                                    .appModule(AppModule(this))
                                                    .utilsModule(UtilsModule())
                                                    .receiversModule(ReceiversModule())
                                                    .build()
}