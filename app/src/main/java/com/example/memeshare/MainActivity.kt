package com.example.memeshare

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {
    var currentMemeURL: String? = null
    lateinit var loadingProgressBar: ProgressBar
    lateinit var memeImage : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        memeImage=findViewById(R.id.memeImage)
        loadingProgressBar=findViewById(R.id.loadingBar)
        loadMeme()
        var nextButton=findViewById<Button>(R.id.nextButton)
        var shareButton=findViewById<Button>(R.id.shareButton)
        nextButton.setOnClickListener(){
            loadMeme()
        }
        shareButton.setOnClickListener(){
            var intent=Intent(Intent.ACTION_SEND)
            intent.type="text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, currentMemeURL)
            val chooser = Intent.createChooser(intent, "Share this meme with ... ")
            startActivity(chooser)
        }

    }
    private fun loadMeme(){
        var blackImage:Int = R.drawable.black
        memeImage.setImageResource(blackImage)
        loadingProgressBar.visibility=View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val api = "https://meme-api.herokuapp.com/gimme"
        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, api, null,
            { response ->
                currentMemeURL = response.getString("url")
                Glide.with(this).load(currentMemeURL).listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingProgressBar.visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingProgressBar.visibility=View.GONE
                        return false
                    }

                }).into(memeImage)
            },
            {  })

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

}