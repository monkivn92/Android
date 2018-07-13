package com.vdroid.directnotification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import android.util.Log
import android.support.v4.app.NotificationManagerCompat




class CustomBroadCastReceiver : BroadcastReceiver()
{
    companion object
    {
        val ACTION_SHOW_TOAST = "com.vdroid.AAAAAAAA"
        private val KEY_TEXT_REPLY = "key_text_reply"
    }

    override fun onReceive(context: Context, intent: Intent)
    {

        if (intent.action == ACTION_SHOW_TOAST)
        {
            val text = "Broadcast Received!"
            val duration = Toast.LENGTH_SHORT
            val remoteInput = RemoteInput.getResultsFromIntent(intent)

            val toast = Toast.makeText(context, remoteInput?.getCharSequence(KEY_TEXT_REPLY), duration)
            toast.show()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            {
                val reply = "reply more"
                val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel(reply)
                        .build()
                val intent = Intent(context, CustomBroadCastReceiver::class.java)
                intent.action = CustomBroadCastReceiver.ACTION_SHOW_TOAST

                val pendingIntent = PendingIntent.getBroadcast(context, 123, intent, 0)

                val action = NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, reply, pendingIntent)
                        .addRemoteInput(remoteInput)
                        .build()

                val mBuilder = NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentText("Received at...")
                        .addAction(action)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText("Received at broadcastreceiver Received at broadcastreceiverReceived at broadcastreceiverReceived at broadcastreceiverReceived at broadcastreceiverReceived at broadcastreceiverReceived at broadcastreceiver Received at broadcastreceiver Received at broadcastreceiver"))
                val notificationManager = NotificationManagerCompat.from(context)
                //must use the same id with sent notification to update the sent notification
                notificationManager.notify(1000, mBuilder.build())
            }
        }
    }

}