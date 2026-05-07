package com.example.productsapi.services

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.IBinder
import com.example.productsapi.notification.CatalogNotificationHelper

class NetworkStatusForegroundService : Service() {
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate() {
        super.onCreate()
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        registerNetworkCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = CatalogNotificationHelper.buildNetworkUnavailableNotification(this)
        startForeground(CatalogNotificationHelper.NETWORK_NOTIFICATION_ID, notification)

        if (isNetworkAvailable()) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        networkCallback?.let(connectivityManager::unregisterNetworkCallback)
        networkCallback = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerNetworkCallback() {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        networkCallback = callback
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}