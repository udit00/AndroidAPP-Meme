package com.example.memeshare

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.navigation.NavigationBarMenu
import com.google.android.material.navigation.NavigationBarView
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var currentMemeURL: String = ""
    var currentIndex = 0
    lateinit var navigationBar: NavigationBarView
    lateinit var loadingProgressBar: ProgressBar
    lateinit var memeImage : ImageView
    lateinit var memeList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        memeList = ArrayList(10)
        navigationBar = findViewById(R.id.navigationBar)
        val mOnNavigationItemClickListener = NavigationBarView.OnItemSelectedListener {
            when(it.itemId){

                R.id.share -> {

//                    navigationBar.menu.findItem(R.id.share).isVisible = false
//                    navigationBar.menu.findItem(R.id.back).isVisible = false
//                    navigationBar.menu.findItem(R.id.next).isVisible = false
                    navigationBar.isEnabled = false
                    val intent=Intent(Intent.ACTION_SEND)
                    intent.type="text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, currentMemeURL)
                    val chooser = Intent.createChooser(intent, "Share this meme with ... ")
                    startActivity(chooser)
                    navigationBar.menu.findItem(R.id.share).isEnabled = true
                    navigationBar.menu.findItem(R.id.back).isEnabled = true
                    navigationBar.menu.findItem(R.id.next).isEnabled = true
                    return@OnItemSelectedListener true
                }
                R.id.back -> {
                    loadingProgressBar.visibility = View.VISIBLE
                    if(currentIndex < 1) {
                        Toast.makeText(this, "No meme was loaded before this current meme. Please " +
                                "press next to load more meme", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        loadMemeFromStorage(memeList[--currentIndex])
                        currentMemeURL = memeList[currentIndex]
                    }
//                    navigationBar.menu.findItem(R.id.share).isEnabled = true
                    return@OnItemSelectedListener true
                }
                R.id.next -> {
                    loadingProgressBar.visibility = View.VISIBLE
                    navigationBar.menu.findItem(R.id.next).isCheckable = false
                    if(currentIndex+1==memeList.size) {
                        loadMeme()
                        currentMemeURL = memeList[currentIndex++]
                    }
                    else if(currentIndex == memeList.size){
                        loadMemeFromStorage(currentMemeURL)
                    }
                    else {
                        if(currentMemeURL == memeList[currentIndex]) loadMemeFromStorage(memeList[++currentIndex])
                        else loadMemeFromStorage(memeList[currentIndex++])
                        currentMemeURL = memeList[currentIndex]
                    }
                    return@OnItemSelectedListener true
                }
            }
            false
        }
        navigationBar.setOnItemSelectedListener(mOnNavigationItemClickListener)
        memeImage=findViewById(R.id.memeImage)
        loadingProgressBar=findViewById(R.id.loadingBar)
        loadMeme()
    }
    private fun processImage(url: String){
        Glide.with(this).load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    loadingProgressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    loadingProgressBar.visibility = View.GONE
                    return false
                }

            }).into(memeImage)
    }
    private fun loadMeme() {
        memeImage.setImageResource(R.drawable.black)
        loadingProgressBar.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this, )
        val api = "https://meme-api.herokuapp.com/gimme"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, api, null,
            { response ->
                memeList.add(response.getString("url"))
                processImage(memeList[currentIndex])
            }, {
                Toast.makeText(this, "Something is wrong, Try restart your internet.", Toast.LENGTH_SHORT).show()
            })
        queue.add(jsonObjectRequest)
    }
    fun loadMemeFromStorage(url: String) {
        memeImage.setImageResource(R.drawable.black)
        loadingProgressBar.visibility = View.VISIBLE
        processImage(url)
    }
}