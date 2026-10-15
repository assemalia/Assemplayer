package com.assemalia.assemplayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat


class MusicService :Service() ,AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer :MediaPlayer? =null
    private lateinit var mediaSession :MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder? {
       mediaSession=MediaSessionCompat(baseContext,"My Music")
      return myBinder
    }

  inner class  MyBinder:Binder(){
      fun currentService():MusicService{
          return this@MusicService
      }
  }
    @SuppressLint("SuspiciousIndentation")
    fun showNotification(playPauseBtn :Int){
        val intent = Intent(baseContext, MainActivity::class.java)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val prevIntent = Intent(baseContext, NotificationReciver::class.java).setAction(Applicationclass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReciver::class.java).setAction(Applicationclass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReciver::class.java).setAction(Applicationclass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReciver::class.java).setAction(Applicationclass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if(imgArt != null){
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.i_vid)
        }
        val notification = androidx.core.app.NotificationCompat.Builder(baseContext,Applicationclass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].album)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon,"Previous",prevPendingIntent)
            .addAction(playPauseBtn,"Play",playPendingIntent)
            .addAction(R.drawable.next_icon,"Next",nextPendingIntent)
            .addAction(R.drawable.exit_icon,"Exit",exitPendingIntent)
            //.setContentIntent(pendingIntent)
            .build()


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val playbackSpeed = if(PlayerActivity.isPlaying) 1F else 0F
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())
            val playBackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediaSession.setPlaybackState(playBackState)}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(13, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(13, notification)
        }
    }
   fun createMediaPlayer(){
        try {
            if (PlayerActivity.musicService!!.mediaPlayer==null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvSeekBarStart.text = formatduration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = formatduration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekbarPA.progress = 0
            PlayerActivity.binding.seekbarPA.max = PlayerActivity.musicService!!.mediaPlayer!!.duration
            PlayerActivity.nowplayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
        }catch (e :Exception){return }
    }

    fun seekBarSetup(){
       runnable = Runnable {
           PlayerActivity.binding.tvSeekBarStart.text = formatduration(mediaPlayer!!.currentPosition.toLong())
           PlayerActivity.binding.seekbarPA.progress = mediaPlayer!!.currentPosition
           Handler(Looper.getMainLooper()).postDelayed(runnable,200)
       }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)

    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0){
            //pause music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            Nowplaying.binding.playPauseBTnNp.setIconResource(R.drawable.play_icon)
            showNotification(R.drawable.play_icon)
            PlayerActivity.isPlaying =false
            mediaPlayer!!.pause()
        }else{
            //play music
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            showNotification(R.drawable.pause_icon)
            PlayerActivity.isPlaying =true
            mediaPlayer!!.start()
        }
    }
}