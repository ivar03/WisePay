package com.ivar7284.rbi_pay.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivar7284.rbi_pay.R
import com.ivar7284.rbi_pay.dataclasses.VideoItem

class VideoAdapter(private val context: Context, private val videoList: List<VideoItem>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

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
        }

        holder.itemView.setOnClickListener {
            holder.webView.loadUrl(videoUrl)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}
