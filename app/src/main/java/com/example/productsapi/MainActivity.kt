package com.example.productsapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.productsapi.ui.screens.CatalogScreen
import com.example.productsapi.ui.theme.ProductsApiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilita el diseño de borde a borde (característica clave de API 36)
        enableEdgeToEdge()

        setContent {
            ProductsApiTheme { // El tema generado por Android Studio
                // Llamamos a la pantalla que creamos en ui.screens
                CatalogScreen()
            }
        }
    }
}