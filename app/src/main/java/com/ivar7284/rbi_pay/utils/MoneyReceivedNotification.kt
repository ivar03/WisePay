package com.ivar7284.rbi_pay.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ivar7284.rbi_pay.R
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.*

class MoneyReceivedNotification : Service() {

    private lateinit var moneyReceived: WebSocketClient

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        connectWebSocket()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Service Running")
            .setContentText("Listening for notifications")
            .setOngoing(true)

        val notification = notificationBuilder.build()
        startForeground(1, notification)
    }

    private fun connectWebSocket() {
        val uri = URI("ws://your_server_ip/ws/notifications/")
        moneyReceived = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("WebSocket", "Connected to server")
            }

            override fun onMessage(message: String?) {
                Log.d("WebSocket", "Message received: $message")
                showNotification(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("WebSocket", "Disconnected from server. Reason: $reason")
                // Retry connection after a delay
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        connectWebSocket()
                    }
                }, 5000)
            }

            override fun onError(ex: Exception?) {
                Log.d("WebSocket", "Error: ${ex?.message}")
            }
        }
        moneyReceived.connect()
    }

    private fun showNotification(message: String?) {
        val notificationBuilder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Notification")
            .setContentText(message)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default_channel_id", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}
