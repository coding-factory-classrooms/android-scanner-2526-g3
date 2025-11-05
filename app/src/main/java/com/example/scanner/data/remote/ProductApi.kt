package com.example.scanner.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): ProductDto
}

