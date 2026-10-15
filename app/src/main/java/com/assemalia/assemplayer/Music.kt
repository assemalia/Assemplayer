package com.assemalia.assemplayer

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music (val id:String, val title:String , val album :String, val artist:String, val duaration :Long =0, val path:String,val artUri:String)
class Playlist{
    lateinit var name: String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdBy: String
    lateinit var createdOn: String
}
class MusicPlaylist{
    var ref : ArrayList<Playlist> =ArrayList()
}
fun formatduration(duaration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duaration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duaration,TimeUnit.MILLISECONDS)-minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%02d:%02d",minutes,seconds)

}
fun getImgArt (path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
fun setSongpotion(icrement :Boolean){
if (!PlayerActivity.repeat){
    if (icrement){
        if (PlayerActivity.musicListPA.size-1 == PlayerActivity.songPosition)
            PlayerActivity.songPosition =0
        else ++PlayerActivity.songPosition
    }else{
        if (0 == PlayerActivity.songPosition)
            PlayerActivity.songPosition = PlayerActivity.musicListPA.size-1
        else --PlayerActivity.songPosition
    }
}

}
fun exitApplication(){
    if (PlayerActivity.musicService!=null){
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus (PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null}
    exitProcess(1)
}
fun favouriteChecker (id: String):Int{
    PlayerActivity.isFavourite =false
FavouriteActivity.favouriteSongs.forEachIndexed { index, music ->
    if (id==music.id){
        PlayerActivity.isFavourite = true
        return index
    }
}
    return -1
}
fun checkPlaylist(playlist: ArrayList<Music>): ArrayList<Music>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}

/*fun setDialogBtnBackground(context: Context, dialog: AlertDialog){
    //setting button text
    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
        MaterialColors.getColor(context, R.color.white, Color.WHITE)
    )
    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
        MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
    )

    //setting button background
    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
}*/