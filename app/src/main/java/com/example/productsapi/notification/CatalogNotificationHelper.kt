package com.example.productsapi.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.productsapi.model.Product

object CatalogNotificationHelper {
    const val NETWORK_NOTIFICATION_ID = 1001

    private const val NETWORK_CHANNEL_ID = "network_status_channel"
    private const val CART_CHANNEL_ID = "cart_notifications_channel"

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

            notificationManager.createNotificationChannel(networkChannel)
            notificationManager.createNotificationChannel(cartChannel)
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

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(product.id, notification)
    }
}