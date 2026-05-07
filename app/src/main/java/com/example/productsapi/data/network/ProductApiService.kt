package com.example.productsapi.data.network

import com.example.productsapi.model.Product
import retrofit2.http.GET

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>
}