package com.example.productsapi.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.productsapi.model.Product
import com.example.productsapi.viewmodel.CatalogState
import com.example.productsapi.viewmodel.CatalogViewModel

    @Composable
    fun CatalogScreen(viewModel: CatalogViewModel = viewModel()) {
        Scaffold(
            topBar = { Text("CatalogApp API 36", style = MaterialTheme.typography.titleLarge) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val currentState = viewModel.state) {
                    is CatalogState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is CatalogState.Error -> Text(currentState.message, Modifier.align(Alignment.Center))
                    is CatalogState.Success -> ProductList(currentState.products)
                }
            }
        }
    }

    @Composable
    fun ProductList(products: List<Product>) {
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
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(product.title, fontWeight = FontWeight.Bold)
                            Text("$${product.price}", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
