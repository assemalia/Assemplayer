package com.assemalia.assemplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.assemalia.assemplayer.databinding.FragmentNowplayingBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class Nowplaying : Fragment() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding :FragmentNowplayingBinding
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =inflater.inflate(R.layout.fragment_nowplaying, container, false)
        binding = FragmentNowplayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playPauseBTnNp.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }
        binding.nextBTnNp.setOnClickListener {
            setSongpotion(icrement = true)
            PlayerActivity.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.SongImgNp)
            binding.songNameNp.text= PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            playMusic()
        }
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songPosition)
            intent.putExtra("class","NowPlaying")

            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.songNameNp.isSelected = true
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_screen).centerCrop())
                .into(binding.SongImgNp)
            binding.songNameNp.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) binding.playPauseBTnNp.setIconResource(R.drawable.pause_icon)
            else binding.playPauseBTnNp.setIconResource(R.drawable.play_icon)
        }
    }
   private fun playMusic(){
       PlayerActivity.musicService!!.mediaPlayer!!.start()
       binding.playPauseBTnNp.setIconResource(R.drawable.pause_icon)
       PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
       PlayerActivity.binding.nextBTnPa.setIconResource(R.drawable.pause_icon)
       PlayerActivity.isPlaying =true
   }
   private fun pauseMusic(){
       PlayerActivity.musicService!!.mediaPlayer!!.pause()
       binding.playPauseBTnNp.setIconResource(R.drawable.play_icon)
       PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
       PlayerActivity.binding.nextBTnPa.setIconResource(R.drawable.play_icon)
       PlayerActivity.isPlaying =false
   }
}