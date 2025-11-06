package com.example.scanner.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    @GET("api/v2/product/{barcode}?fields=product_name,brands_tags,image_front_url,code,categories_tags_fr,allergens_tags_fr,ingredients_tags_fr,quantity")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): ProductDto
}