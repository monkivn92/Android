package com.vdroid.daggertwopart2

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ChatModule
{

    @ChatScope
    @Provides
    fun provideChatInteract(context: Context, iDataRepository : IDataRepository, iChatStateController : IChatStateController) : IChatInteract = ChatInteract(context, iDataRepository, iChatStateController)


    @ChatScope
    @Provides
    fun provideChatStateController(rxUtils: RxUtils) : IChatStateController = ChatStateController(rxUtils)
}