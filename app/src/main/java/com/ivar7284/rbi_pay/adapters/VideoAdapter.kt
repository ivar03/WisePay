package com.ivar7284.rbi_pay.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.rbi_pay.R
import com.ivar7284.rbi_pay.dataclasses.VideoItem

class VideoAdapter(
    private val context: Context,
    private val videoList: List<VideoItem>,
    private val stopAutoScroll: () -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var customView: View? = null

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val webView: WebView = itemView.findViewById(R.id.youtube_web_view)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val title: TextView = itemView.findViewById(R.id.video_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = videoList[position]
        holder.title.text = videoItem.title

        val webSettings: WebSettings = holder.webView.settings
        webSettings.javaScriptEnabled = true

        val videoUrl = "https://www.youtube.com/embed/${videoItem.videoId}"
        holder.webView.loadData(
            "<html><body><iframe width=\"100%\" height=\"100%\" src=\"$videoUrl\" frameborder=\"0\" allowfullscreen></iframe></body></html>",
            "text/html",
            "utf-8"
        )

        holder.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    holder.progressBar.visibility = View.GONE
                } else {
                    holder.progressBar.visibility = View.VISIBLE
                }
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }
                customView = view
                customViewCallback = callback
                (context as? Activity)?.let {
                    it.window.decorView.findViewById<FrameLayout>(android.R.id.content).addView(
                        customView,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                    it.window.decorView.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            )
                }
            }

            override fun onHideCustomView() {
                (context as? Activity)?.let {
                    it.window.decorView.findViewById<FrameLayout>(android.R.id.content).removeView(customView)
                    customView = null
                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                    it.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        }

        holder.itemView.setOnClickListener {
            stopAutoScroll() // Stop the auto-scroll when a video is played
            holder.webView.loadUrl(videoUrl)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}
