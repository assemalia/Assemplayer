package com.assemalia.assemplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.assemalia.assemplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    lateinit var binding :ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"
        binding.aboutText.text= aboutText()
    }
    private fun aboutText():String{
        return "Developed By : ASSEM ALIA" + "\n\n If you want to provide feedback,I will love to hear that ,"
    }
}