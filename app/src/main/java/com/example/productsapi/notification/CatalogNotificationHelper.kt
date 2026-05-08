package com.example.productsapi.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.productsapi.model.Product

object CatalogNotificationHelper {
    const val NETWORK_NOTIFICATION_ID = 1001
    const val ACTION_RESTORE_WIFI_NOTIFICATION =
        "com.example.productsapi.notification.RESTORE_WIFI_NOTIFICATION"
    private const val WIFI_NOTIFICATION_ID = 2001
    private const val NETWORK_CHANNEL_ID = "network_status_channel"
    private const val CART_CHANNEL_ID = "cart_notifications_channel"
    private const val WIFI_CHANNEL_ID = "wifi_status_channel"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            val networkChannel = NotificationChannel(
                NETWORK_CHANNEL_ID,
                "Alertas obligatorias de red",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones persistentes mientras no hay conexión a internet."
            }

            val cartChannel = NotificationChannel(
                CART_CHANNEL_ID,
                "Carrito de compras",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Mensajes informativos al agregar productos al carrito."
            }

            val wifiChannel = NotificationChannel(
                WIFI_CHANNEL_ID,
                "Estado de Wi-Fi",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificación fija para mostrar el estado de Wi-Fi."
            }

            notificationManager.createNotificationChannel(networkChannel)
            notificationManager.createNotificationChannel(cartChannel)
            notificationManager.createNotificationChannel(wifiChannel)
        }
    }

    fun buildNetworkUnavailableNotification(context: Context): Notification {
        createChannels(context)

        return NotificationCompat.Builder(context, NETWORK_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Sin conexión a internet")
            .setContentText("No hay Wi-Fi o red disponible. Revisa tu conexión para actualizar el catálogo.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("No hay Wi-Fi o red disponible. Revisa tu conexión para actualizar el catálogo.")
            )
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun showWifiStatusNotification(context: Context) {
        createChannels(context)

        val notification = NotificationCompat.Builder(context, WIFI_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.presence_online)
            .setContentTitle("Wi-Fi activo")
            .setContentText("Conectado a una red Wi-Fi. Esta notificación está bloqueada.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Conectado a una red Wi-Fi. " +
                                "Esta notificación está bloqueada para que no se pueda deslizar."
                    )
            )
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setOngoing(true)
            .setAutoCancel(false)
            .setDeleteIntent(createWifiRestorePendingIntent(context))
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
            .apply {
                flags = flags or Notification.FLAG_ONGOING_EVENT or Notification.FLAG_NO_CLEAR
            }

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.notify(WIFI_NOTIFICATION_ID, notification)
    }

    private fun createWifiRestorePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, WifiNotificationDismissedReceiver::class.java).apply {
            action = ACTION_RESTORE_WIFI_NOTIFICATION
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, WIFI_NOTIFICATION_ID, intent, flags)
    }
    fun showCartSuccessNotification(context: Context, product: Product) {
        createChannels(context)

        val notification = NotificationCompat.Builder(context, CART_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.checkbox_on_background)
            .setContentTitle("Producto añadido")
            .setContentText("${product.title} se ha añadido al carrito correctamente.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${product.title} se ha añadido al carrito correctamente.")
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.notify(product.id, notification)
    }
}