package com.assemalia.assemplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.assemalia.assemplayer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"
    }
}