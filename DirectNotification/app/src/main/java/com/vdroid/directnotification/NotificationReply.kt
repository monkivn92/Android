package com.vdroid.directnotification

import android.os.Bundle
import android.content.Intent
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.content.Intent.getIntent
import android.support.v4.app.RemoteInput
import android.widget.TextView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_notification_reply.*


class NotificationReply : AppCompatActivity()
{
    internal var mRequestCode = 1000

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_reply)
        val textView = txt1
        textView.text = getMessageText(intent)

        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("Received ${textView.text}")

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(mRequestCode, mBuilder.build())
    }

    private fun getMessageText(intent: Intent): CharSequence?
    {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        return if (remoteInput != null)
                {
                    remoteInput!!.getCharSequence(KEY_TEXT_REPLY)
                }
                else null
    }

    companion object {

        private val KEY_TEXT_REPLY = "key_text_reply"
    }
}