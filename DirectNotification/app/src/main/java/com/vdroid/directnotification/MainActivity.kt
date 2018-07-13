package com.vdroid.directnotification

import android.app.Notification
import android.app.RemoteInput
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import android.support.v4.app.NotificationCompat
import android.content.IntentFilter






class MainActivity : AppCompatActivity()
{
    private val KEY_TEXT_REPLY = "key_text_reply"
    var mRequestCode = 1000
    private val CHANNEL_ID: String = "abcdef"
    lateinit var mReceiver : CustomBroadCastReceiver
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentFilter = IntentFilter()
        intentFilter.addAction(CustomBroadCastReceiver.ACTION_SHOW_TOAST)

        mReceiver = CustomBroadCastReceiver()
        registerReceiver(mReceiver, intentFilter)

        test_btn.setOnClickListener { v->
            pushNotification()
        }

    }

    override fun onDestroy()
    {

        unregisterReceiver(mReceiver)

        super.onDestroy()
    }


    fun createNotification()
    {
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("This is a noti title")
                .setContentText("This is the context of this noti")
                .setStyle(NotificationCompat.BigTextStyle().bigText("Set Syle Big Text"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    fun pushNotification()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            val reply = "reply"
            val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
                                .setLabel(reply)
                                .build()

            val intent = Intent(this@MainActivity, CustomBroadCastReceiver::class.java)
            intent.action = CustomBroadCastReceiver.ACTION_SHOW_TOAST

            val pendingIntent = PendingIntent.getBroadcast(this, 123, intent, 0)

            val action = Notification.Action.Builder(R.mipmap.ic_launcher_round, reply, pendingIntent)
                    .addRemoteInput(remoteInput)
                    .build()

            val builder = Notification.Builder(applicationContext)
                    .addAction(action)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("INLINE REPLY...")
                    .setContentText("hello...")
                    .setContentIntent(pendingIntent)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(mRequestCode, builder.build())

        }

    }
}


