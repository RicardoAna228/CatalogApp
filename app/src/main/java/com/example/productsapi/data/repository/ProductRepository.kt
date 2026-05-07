package com.example.productsapi.data.repository

import com.example.productsapi.data.network.ProductApiService
import com.example.productsapi.model.Product

class ProductRepository(private val api: ProductApiService) {
    suspend fun fetchAllProducts(): List<Product> = api.getProducts()
}