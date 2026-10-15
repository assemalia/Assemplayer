package com.assemalia.assemplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationReciver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
    when(intent?.action){
        Applicationclass.PREVIOUS -> prevnextSong(icrement = false, context = context!!)
        Applicationclass.NEXT -> prevnextSong(icrement = true, context = context!!)
        Applicationclass.PLAY -> if (PlayerActivity.isPlaying) pausemusic() else playmusic()
        Applicationclass.EXIT -> {
            exitApplication()
        }

    }
    }
    private fun playmusic(){
        PlayerActivity.isPlaying =true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        Nowplaying.binding.playPauseBTnNp.setIconResource(R.drawable.pause_icon)
    }
    private fun pausemusic(){
        PlayerActivity.isPlaying =false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        Nowplaying.binding.playPauseBTnNp.setIconResource(R.drawable.play_icon)
    }
    private fun prevnextSong(icrement: Boolean,context: Context){
        setSongpotion(icrement = icrement)
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(PlayerActivity.binding.SongImgPA)
        PlayerActivity.binding.songnamePA.text= PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
            .into(Nowplaying.binding.SongImgNp)
        Nowplaying.binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playmusic()
        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if (PlayerActivity.isFavourite) PlayerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favorite_icon)
        else PlayerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favorite_empty)
    }
}