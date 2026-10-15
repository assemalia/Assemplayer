package com.assemalia.assemplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.assemalia.assemplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle :ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
       lateinit var MusicListMA :ArrayList<Music>
       lateinit var musicListSearch :ArrayList<Music>
       var search : Boolean =false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestRuntimepermission()
        setTheme(R.style.Theme_Assemplayer)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //for navigation drower
        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (requestRuntimepermission())
        {initilizelayout()
            //for retrieving favourites data using shared preferences
            FavouriteActivity.favouriteSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs",null)
            val typeToken =object :TypeToken<ArrayList<Music>>(){}.type
            if (jsonString!=null){
                val data:ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
                FavouriteActivity.favouriteSongs.addAll(data)
            }
            PlaylistActivity.musicPlaylist = MusicPlaylist()
            val jsonStringPlaylist = editor.getString("MusicPlaylist",null)

            if (jsonStringPlaylist!=null){
                val dataPlaylist:MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist,MusicPlaylist::class.java)
                PlaylistActivity.musicPlaylist = dataPlaylist
            }
        }
        binding.shufflebtn.setOnClickListener{
            val intent = Intent (this@MainActivity ,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)
        }
        binding.favouriteBtn.setOnClickListener {
            val intent = Intent (this@MainActivity ,FavouriteActivity::class.java)
            startActivity(intent)
        }
        binding.Playlist.setOnClickListener {
            val intent = Intent (this@MainActivity ,PlaylistActivity::class.java)
            startActivity(intent)
        }
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navFeedBack-> startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
                R.id.navSettings-> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                R.id.navAbout-> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                R.id.Exit-> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close app ?")
                        .setPositiveButton("Yes"){ _, _ ->
                           exitApplication()
                        }
                        .setNegativeButton("No"){dialog,_ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }
    }

    //the permission needed to read audio via MediaStore changed in Android 13 (API 33)
    private val audioReadPermission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.READ_MEDIA_AUDIO
        else
            android.Manifest.permission.READ_EXTERNAL_STORAGE

    //for requesting permission
    private fun requestRuntimepermission():Boolean{
        if (ActivityCompat.checkSelfPermission(this, audioReadPermission) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(audioReadPermission),13)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==13){if (grantResults.isNotEmpty()&&grantResults[0] ==PackageManager.PERMISSION_GRANTED)
        { Toast.makeText(this,"permission granted",Toast.LENGTH_LONG).show()
         initilizelayout()
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(audioReadPermission),13)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun initilizelayout (){
        search = false
        MusicListMA = getAllAudio()
        binding.musicRv.setHasFixedSize(true)
        binding.musicRv.setItemViewCacheSize(13)
        binding.musicRv.layoutManager=LinearLayoutManager(this@MainActivity)
        musicAdapter= MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRv.adapter = musicAdapter
        binding.totalsongs.text = "Total Songs: "+musicAdapter.itemCount
    }

    @SuppressLint("Recycle", "Range")
    private fun getAllAudio():ArrayList<Music>{
        val templist=ArrayList<Music>()
        val selection= MediaStore.Audio.Media.IS_MUSIC + " !=0 "
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,
        MediaStore.Audio.Media.DATE_ADDED + " DESC",null)
        if(cursor != null){
            if (cursor.moveToFirst())
                do{
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val duritionC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIDC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri,albumIDC).toString()
                    val music = Music(id=idC ,title=titleC,album=albumC,artist=artistC, duaration = duritionC,path =pathC, artUri = artUriC)
                    val file =File(music.path)
                    if (file.exists()) templist.add(music)
                }while (cursor.moveToNext())
                cursor.close()
        }
        return templist
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService!= null){
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favouriteSongs)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu,menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
             if (newText != null){
                 val userInput = newText.lowercase()
                 for (song in MusicListMA){
                     if (song.title.lowercase().contains(userInput))
                         musicListSearch.add(song)
                     search =true
                     musicAdapter.updateMusicList(searchList = musicListSearch)

                 }
             }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}