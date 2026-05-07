package com.example.productsapi.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsapi.data.network.RetrofitClient
import com.example.productsapi.data.repository.ProductRepository
import com.example.productsapi.model.Product
import kotlinx.coroutines.launch

sealed interface CatalogState {
    object Loading : CatalogState
    data class Success(val products: List<Product>) : CatalogState
    data class Error(val message: String) : CatalogState
}

class CatalogViewModel : ViewModel() {
    // Estado observable por la UI
    var state: CatalogState by mutableStateOf(CatalogState.Loading)
        private set

    private val repository = ProductRepository(RetrofitClient.apiService)

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            state = CatalogState.Loading
            try {
                val list = repository.fetchAllProducts()
                state = CatalogState.Success(list)
            } catch (e: Exception) {
                state = CatalogState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}