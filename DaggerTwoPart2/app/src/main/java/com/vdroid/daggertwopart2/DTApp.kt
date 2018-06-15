package com.vdroid.daggertwopart2

import android.app.Application

class DTApp : Application()
{

    companion object
    {
        private var appCom : AppComponent? = null
        private var chatCom : ChatComponent? = null
        private var scCom : SCComponent? = null

        protected lateinit var instance : DTApp

        fun getApp() : DTApp = instance

        fun getChatCom() : ChatComponent? = chatCom
        fun getAppCom() : AppComponent? = appCom
    }



    override fun onCreate()
    {
        super.onCreate()
        instance = this
        appCom = DaggerAppComponent.builder()
                    .appModule(AppModule(instance))
                    .receiversModule(ReceiversModule())
                    .dataModule(DataModule())
                    .utilsModule(UtilsModule())
                    .build()

    }

    fun  plusChatComponent() : ChatComponent?
    {
        // always get only one instance
        if (chatCom == null)
        {
            // start lifecycle of chatComponent
            chatCom = appCom?.plusChatComponent(ChatModule())
        }
        return chatCom
    }

    fun clearChatComponent()
    {
        chatCom = null
    }

    fun  plusSCComponent() : SCComponent?
    {
        // always get only one instance
        if (scCom == null)
        {
            // start lifecycle of scComponent
            scCom = chatCom?.plusSCComponent(SCModule())
        }
        return scCom
    }

    fun clearSCComponent()
    {
        // end lifecycle of scComponent
        scCom = null
    }
}