package com.vdroid.dictening

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import android.view.Gravity
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View


class ChatHeadService : Service()
{
    private var mWindowManager: WindowManager? = null
    private var mChatHeadView: View? = null


    override fun onBind(intent: Intent): IBinder?
    {
        return null
    }


    override fun onCreate()
    {
        super.onCreate()
        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.chathead, null)


        //Add the view to the window.
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        //Specify the chat head position
        //Initially view will be added to top-left corner
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mChatHeadView, params)


    }


    override fun onDestroy()
    {
        super.onDestroy()
        if (mChatHeadView != null) mWindowManager!!.removeView(mChatHeadView)
    }
}
