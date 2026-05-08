package com.example.productsapi.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.productsapi.model.Product
import com.example.productsapi.notification.CatalogNotificationHelper
import com.example.productsapi.services.NetworkStatusForegroundService
import com.example.productsapi.viewmodel.CatalogState
import com.example.productsapi.viewmodel.CatalogViewModel

@Composable
fun CatalogScreen(viewModel: CatalogViewModel = viewModel()) {
    val context = LocalContext.current

    NotificationPermissionHandler()
    NetworkMandatoryNotificationHandler()

    Scaffold(
        topBar = { Text("CatalogApp", style = MaterialTheme.typography.titleLarge) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val currentState = viewModel.state) {
                is CatalogState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is CatalogState.Error -> Text(
                    currentState.message,
                    Modifier.align(Alignment.Center)
                )

                is CatalogState.Success -> ProductList(
                    products = currentState.products,
                    onAddToCart = { product ->
                        CatalogNotificationHelper.showCartSuccessNotification(
                            context.applicationContext,
                            product
                        )
                    },
                    onShowWifiNotification = {
                        CatalogNotificationHelper.showWifiStatusNotification(
                            context.applicationContext
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onAddToCart: (Product) -> Unit,
    onShowWifiNotification: () -> Unit
) {
    LazyColumn {
        items(products) { product ->
            Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    // Carga de imagen con Coil
                    AsyncImage(
                        model = product.image,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                    Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(product.title, fontWeight = FontWeight.Bold)
                        Text("$${product.price}", color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { onAddToCart(product) }) {
                            Text("Agregar al carrito")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick =  onShowWifiNotification ) {
                            Text("Notificación")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationPermissionHandler() {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Permiso de notificaciones denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun NetworkMandatoryNotificationHandler() {
    val context = LocalContext.current
    val appContext = context.applicationContext
    var hasNetwork by remember { mutableStateOf(isNetworkAvailable(appContext)) }

    DisposableEffect(appContext) {
        val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java)
        val mainHandler = Handler(Looper.getMainLooper())
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                mainHandler.post { hasNetwork = true }
            }

            override fun onLost(network: Network) {
                mainHandler.post { hasNetwork = isNetworkAvailable(appContext) }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                mainHandler.post {
                    hasNetwork = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    )
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        hasNetwork = isNetworkAvailable(appContext)

        onDispose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    LaunchedEffect(hasNetwork) {
        val serviceIntent = Intent(appContext, NetworkStatusForegroundService::class.java)
        if (hasNetwork) {
            appContext.stopService(serviceIntent)
        } else {
            ContextCompat.startForegroundService(appContext, serviceIntent)
        }
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
