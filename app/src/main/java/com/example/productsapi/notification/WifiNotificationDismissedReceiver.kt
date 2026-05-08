package com.example.productsapi.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WifiNotificationDismissedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CatalogNotificationHelper.ACTION_RESTORE_WIFI_NOTIFICATION) {
            CatalogNotificationHelper.showWifiStatusNotification(context.applicationContext)
        }
    }
}