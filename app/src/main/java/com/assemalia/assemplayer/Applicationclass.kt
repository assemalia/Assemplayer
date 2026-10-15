package com.assemalia.assemplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class Applicationclass:Application() {
    companion object{
        const val CHANNEL_ID= "channel"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS ="previous"
        const val EXIT ="exit"
    }
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(CHANNEL_ID,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description ="This is a important channel for showing song !!"
            val notificationManager =getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

   /* private fun CreateNoticationchannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val serviceChannel = NotificationChannel(
                CHANNAL_ID,"my service channel",NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }*/

}