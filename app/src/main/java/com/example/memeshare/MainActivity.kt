package com.example.memeshare

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.loader.content.AsyncTaskLoader
import com.android.volley.AsyncNetwork
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.navigation.NavigationBarView
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var currentMemeURL: String
    var currentIndex = 0
    lateinit var loadingProgressBar: ProgressBar
    lateinit var memeImage : ImageView
    lateinit var memeList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currentMemeURL = ""
        memeList = ArrayList<String>(0)
        val navigationBar = findViewById<NavigationBarView>(R.id.navigationBar)
        val mOnNavigationItemClickListener = NavigationBarView.OnItemSelectedListener {
            when(it.itemId){
                R.id.share -> {
                    var intent=Intent(Intent.ACTION_SEND)
                    intent.type="text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, currentMemeURL)
                    val chooser = Intent.createChooser(intent, "Share this meme with ... ")
                    startActivity(chooser)
                    return@OnItemSelectedListener true
                }
                R.id.back -> {
                    if(currentIndex < 1) {
                        Toast.makeText(this, "No meme was loaded before this current meme. Please " +
                                "press next to load more meme", Toast.LENGTH_SHORT).show()
//                        Log.d("sizeBackFunc", currentIndex.toString())
                        return@OnItemSelectedListener true
                    }
                    else {
                        loadMemeFromStorage(memeList[--currentIndex])
                        currentMemeURL = memeList[currentIndex]
//                        Log.d("size1back", currentIndex.toString())
                        return@OnItemSelectedListener true
                    }
                }
                R.id.next -> {
                    if(currentIndex+1==memeList.size) {
                        loadMeme()
                        currentIndex++
                        currentMemeURL = memeList[currentIndex-1]
                        return@OnItemSelectedListener true
                    }
                    else {
                        if(currentMemeURL == memeList[currentIndex]) loadMemeFromStorage(memeList[++currentIndex])
                        else loadMemeFromStorage(memeList[currentIndex++])
                        currentMemeURL = memeList[currentIndex]
//                        Log.d("size1next", currentIndex.toString())
                        return@OnItemSelectedListener true
                    }
                }
            }
            false
        }
        navigationBar.setOnItemSelectedListener(mOnNavigationItemClickListener)
        memeImage=findViewById(R.id.memeImage)
        loadingProgressBar=findViewById(R.id.loadingBar)
        loadMeme()
    }
    fun processImage(url: String){
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
    fun loadMemeFromStorage(url: String){
        memeImage.setImageResource(R.drawable.black)
        loadingProgressBar.visibility = View.VISIBLE
        processImage(url)
    }
}